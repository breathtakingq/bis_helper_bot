package ru.mybot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class Bot extends TelegramLongPollingBot {
    private String token;
    private Database db;

    public Bot(String token) {
        this.token = token;
        this.db = new Database();
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return "bis_helper_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                long chatId = update.getMessage().getChatId();
                String text = update.getMessage().getText();

                try {
                    MongoLogger.get().logMessage(chatId, text);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (text.equals("/start")) {
                    showMenu(chatId);
                }
            }

            if (update.hasCallbackQuery()) {
                String data = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();

                if (data.equals("services")) {
                    showServices(chatId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMenu(long chatId) throws Exception {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Добро пожаловать!\nВыберите действие:");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText("Услуги");
        btn.setCallbackData("services");

        rows.add(Arrays.asList(btn));
        keyboard.setKeyboard(rows);
        msg.setReplyMarkup(keyboard);

        execute(msg);
    }

    private void showServices(long chatId) throws Exception {
        List<Service> services = db.getServices();

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Наши услуги:");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Service service : services) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(service.name + " (" + service.duration + " мин)");
            btn.setCallbackData("service_" + service.id);
            rows.add(Arrays.asList(btn));
        }

        keyboard.setKeyboard(rows);
        msg.setReplyMarkup(keyboard);

        execute(msg);
    }
}