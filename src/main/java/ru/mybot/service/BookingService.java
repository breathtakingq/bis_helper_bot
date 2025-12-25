package ru.mybot.service;

import ru.mybot.dataprovider.DataProvider;
import ru.mybot.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BookingService {

    private final DataProvider dataProvider;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookingService(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public String createBooking(long telegramId, String dateStr, String timeStr, String serviceName) {
        try {
            // Парсинг даты и времени
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            LocalTime time = LocalTime.parse(timeStr, TIME_FORMATTER);

            // Поиск услуги
            ServiceDTO service = findServiceByName(serviceName)
                    .orElseThrow(() -> new IllegalArgumentException("Услуга не найдена: " + serviceName));

            // Получение/создание клиента
            ClientDTO client = dataProvider.getClientByTelegramId(telegramId)
                    .orElseGet(() -> {
                        ClientDTO newClient = new ClientDTO(0, telegramId, "Клиент " + telegramId, "Не указан");
                        int clientId = dataProvider.createClient(newClient);
                        if (clientId > 0) {
                            newClient.setId(clientId);
                        }
                        return newClient;
                    });


            // Проверка занятости слота
            if (isSlotBusy(date, time, service.getDuration())) {
                return "❌ Слот занят. Выберите другое время.";
            }

            // Получение мастера (пока мастер = 1)
            int masterId = 1;
            MasterDTO master = dataProvider.getMasterById(masterId);
            if (master == null) {
                return "Ошибка: мастер не найден.";
            }

            // Проверка лимита записей на день
            int todayCount = dataProvider.countAppointmentsByMasterAndDate(masterId, date);
            if (todayCount >= master.getMaxClientsPerDay()) {
                return "❌ На этот день достигнут лимит записей.";
            }

            // Создание записи
            AppointmentDTO appointment = new AppointmentDTO();
            appointment.setClientId(client.getId());
            appointment.setServiceId(service.getId());
            appointment.setMasterId(masterId);
            appointment.setAppointmentDate(date);
            appointment.setAppointmentTime(time);
            appointment.setStatus("confirmed");

            int appointmentId = dataProvider.createAppointment(appointment);
            if (appointmentId <= 0) {
                return "Ошибка при создании записи.";
            }

            // Создание заказа
            OrderDTO order = new OrderDTO();
            order.setAppointmentId(appointmentId);
            order.setServiceId(service.getId());
            order.setPrice(0);

            dataProvider.createOrder(order);

            return String.format("✅ Запись создана:\n📅 %s\n⏰ %s\n💆 %s",
                    date.format(DATE_FORMATTER), time.format(TIME_FORMATTER), serviceName);

        } catch (IllegalArgumentException e) {
            return "Ошибка: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при создании записи: " + e.getMessage();
        }
    }

    private Optional<ServiceDTO> findServiceByName(String name) {
        return dataProvider.getAllServices().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean isSlotBusy(LocalDate date, LocalTime startTime, int durationMinutes) {
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        return dataProvider.getAllAppointments().stream()
                .filter(a -> a.getAppointmentDate().equals(date))
                .filter(a -> !"cancelled".equalsIgnoreCase(a.getStatus()))
                .anyMatch(a -> {
                    LocalTime aStart = a.getAppointmentTime();
                    ServiceDTO aService = dataProvider.getServiceById(a.getServiceId());
                    if (aService == null) return false;

                    LocalTime aEnd = aStart.plusMinutes(aService.getDuration());

                    return startTime.isBefore(aEnd) && endTime.isAfter(aStart);
                });
    }

}
