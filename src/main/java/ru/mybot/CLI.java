package ru.mybot;

import java.util.Scanner;

public class CLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Database db = new Database();

        System.out.println("=== Beauty Salon CLI ===");
        System.out.println("1. Экспорт записей");
        System.out.println("2. Импорт услуг");
        System.out.println("3. Показать услуги");
        System.out.print("\nВыбор: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                CSV.exportAppointments(db, "appointments.csv");
                break;
            case 2:
                CSV.importServices(db, "services.csv");
                break;
            case 3:
                var services = db.getServices();
                for (var s : services) {
                    System.out.println(s.id + ". " + s.name + " - " + s.duration + " мин");
                }
                break;
        }

        scanner.close();
    }
}