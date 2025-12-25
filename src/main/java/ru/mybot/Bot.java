package ru.mybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.mybot.config.ConfigLoader;
import ru.mybot.dataprovider.DataProvider;
import ru.mybot.dataprovider.PostgresDataProvider;
import ru.mybot.dto.ServiceDTO;
import ru.mybot.service.BookingService;
import ru.mybot.utils.MongoLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    private final String token;
    private final String username;
    private final DataProvider dataProvider;
    private final BookingService bookingService;
    private final Map<Long, BookingSession> userSessions = new HashMap<>();
    private static final Logger log = LogManager.getLogger(Bot.class);

    private static class BookingSession {
        int serviceId;
        LocalDate date;
    }

    public Bot() {
        ConfigLoader config = ConfigLoader.getInstance();
        this.token = config.get("bot.token");
        this.username = config.get("bot.username");

        this.dataProvider = new PostgresDataProvider();
        this.bookingService = new BookingService(this.dataProvider);
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Update received");
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                MongoLogger.get().log(chatId, "message", update.getMessage().getText());

                if (text.equals("/start")) {
                    showServices(chatId);
                }
            } else if (update.hasCallbackQuery()) {
                handleCallback(update);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCallback(Update update) throws Exception {
        String data = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        if (data.startsWith("service_")) {
            int serviceId = Integer.parseInt(data.split("_")[1]);

            userSessions.putIfAbsent(chatId, new BookingSession());
            userSessions.get(chatId).serviceId = serviceId;

            showDates(chatId, messageId);
            MongoLogger.get().log(chatId, "select_service", data);
        }

        else if (data.startsWith("date_")) {
            LocalDate selectedDate = LocalDate.parse(data.split("_")[1]);

            BookingSession session = userSessions.get(chatId);
            if (session != null) {
                session.date = selectedDate;
                showTimeSlots(chatId, messageId, session);
            }
            MongoLogger.get().log(chatId, "confirm_booking", data);
        }

        else if (data.startsWith("time_")) {
            LocalTime selectedTime = LocalTime.parse(data.split("_")[1]);
            BookingSession session = userSessions.get(chatId);

            if (session != null) {
                ServiceDTO service = dataProvider.getServiceById(session.serviceId);
                String serviceName = (service != null) ? service.getName() : "Услуга";

                // вызываем сервис записи
                String result = bookingService.createBooking(
                        update.getCallbackQuery().getFrom().getId(),
                        session.date.toString(),
                        selectedTime.toString(),
                        serviceName
                );

                EditMessageText msg = new EditMessageText();
                msg.setChatId(chatId);
                msg.setMessageId(messageId);
                msg.setText(result);
                execute(msg);

                userSessions.remove(chatId);
            }
        }
    }

    private void showServices(long chatId) throws Exception {
        List<ServiceDTO> services = dataProvider.getAllServices();
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Выберите услугу:");

        List<List<InlineKeyboardButton>> rows = services.stream()
                .map(s -> {
                    InlineKeyboardButton btn = new InlineKeyboardButton();
                    btn.setText(s.getName() + " (" + s.getDuration() + " мин)");
                    btn.setCallbackData("service_" + s.getId());
                    return Collections.singletonList(btn);
                }).toList();
        msg.setReplyMarkup(new InlineKeyboardMarkup(rows));
        execute(msg);
    }

    private void showDates(long chatId, int messageId) throws Exception {
        EditMessageText msg = new EditMessageText();
        msg.setChatId(chatId);
        msg.setMessageId(messageId);
        msg.setText("📅 Выберите дату:");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM (EEE)", new Locale("ru"));

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(date.format(formatter));
            btn.setCallbackData("date_" + date.toString());

            row.add(btn);
            if (row.size() == 2) {
                rows.add(new ArrayList<>(row));
                row.clear();
            }
        }
        if (!row.isEmpty()) rows.add(row);

        msg.setReplyMarkup(new InlineKeyboardMarkup(rows));
        execute(msg);
    }

    private void showTimeSlots(long chatId, int messageId, BookingSession session) throws Exception {
        EditMessageText msg = new EditMessageText();
        msg.setChatId(chatId);
        msg.setMessageId(messageId);
        msg.setText("⏰ Выберите свободное время на " + session.date + ":");

        ServiceDTO service = dataProvider.getServiceById(session.serviceId);
        int duration = (service != null) ? service.getDuration() : 60;

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(19, 0);

        while (start.plusMinutes(duration).isBefore(end) || start.plusMinutes(duration).equals(end)) {
            boolean isBusy = bookingService.isSlotBusy(session.date, start, duration);

            if (!isBusy) {
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText(start.toString());
                btn.setCallbackData("time_" + start.toString());
                row.add(btn);
            }

            if (row.size() == 4) {
                rows.add(new ArrayList<>(row));
                row.clear();
            }
            start = start.plusHours(1);
        }
        if (!row.isEmpty()) rows.add(row);

        if (rows.isEmpty()) {
            msg.setText("😔 На эту дату нет свободных мест.");
        }

        msg.setReplyMarkup(new InlineKeyboardMarkup(rows));
        execute(msg);
    }
}
