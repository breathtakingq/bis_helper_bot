package ru.mybot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.mybot.dataprovider.CSVDataProvider;
import ru.mybot.dataprovider.DataProvider;
import ru.mybot.dataprovider.PostgresDataProvider;
import ru.mybot.dto.AppointmentDTO;
import ru.mybot.dto.ServiceDTO;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            runCLI();
        } else {
            runBot();
        }
    }

    private static void runBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new Bot());
            System.out.println("Бот запущен!");
        } catch (Exception e) {
            System.err.println("Ошибка запуска бота: " + e.getMessage());
        }
    }

    private static void runCLI() {
        Scanner scanner = new Scanner(System.in);

        DataProvider postgres = new PostgresDataProvider(); // это в dataprovider
        CSVDataProvider csvProvider = new CSVDataProvider(); // Создаем отдельно для утилиты экспорта

        while (true) { // Бесконечный цикл, чтобы меню не закрывалось --норм
            System.out.println("\n=== Beauty Salon CLI ===");
            System.out.println("1. Показать все услуги");
            System.out.println("2. Добавить услугу");
            System.out.println("3. Показать записи");
            System.out.println("4. Экспорт записей в CSV"); // <--- НОВЫЙ ПУНКТ
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            if (choice == 0) break;

            switch (choice) { // норм
                case 1:
                    showServices(postgres);
                    break;
                case 2:
                    addService(scanner, postgres);
                    break;
                case 3:
                    showAppointments(postgres);
                    break;
                case 4:
                    // БЕРЕМ данные из Postgres и ОТДАЕМ в CSV -- тоже в dataprovider
                    List<AppointmentDTO> list = postgres.getAllAppointments();
                    csvProvider.exportAppointmentsToCsv(list, "appointments_export.csv");
                    break;
                default:
                    System.out.println("Неверный выбор"); // log.info()
            }
        }
        scanner.close();
    }

    private static void showServices(DataProvider dp) {
        List<ServiceDTO> services = dp.getAllServices();
        System.out.println("\n=== Услуги ===");
        for (ServiceDTO s : services) {
            System.out.println(s.getId() + ". " + s.getName() + " - " + s.getDuration() + " мин"); // .concat
        }
    }

    private static void addService(Scanner scanner, DataProvider dp) {
        System.out.print("Название услуги: ");
        String name = scanner.nextLine();
        System.out.print("Длительность (мин): ");
        int duration = scanner.nextInt();

        ServiceDTO service = new ServiceDTO(0, name, duration);
        int id = dp.createService(service);
        System.out.println("Услуга добавлена с ID: " + id);
    }

    private static void showAppointments(DataProvider dp) {
        System.out.println("\n=== Записи ===");
        dp.getAllAppointments().forEach(apt ->
                System.out.println(apt.getId() + ". Клиент: " + apt.getClientId() +
                        ", Дата: " + apt.getAppointmentDate() + " " + apt.getAppointmentTime())
        );
    }
}
