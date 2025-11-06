package ru.mybot;

import com.opencsv.*;
import java.io.*;
import java.util.*;

public class CSV {

    public static void exportAppointments(Database db, String filename) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            String[] header = {"ID", "Клиент ID", "Услуга ID", "Дата", "Время", "Статус"};
            writer.writeNext(header);

            String[] data = {"1", "10", "1", "2025-11-07", "15:00", "pending"};
            writer.writeNext(data);

            System.out.println("Экспорт завершён: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    public static void importServices(Database db, String filename) {
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            List<String[]> rows = reader.readAll();

            for (int i = 1; i < rows.size(); i++) { // пропускаем заголовок
                String[] row = rows.get(i);
                int masterId = Integer.parseInt(row[0].trim());
                String name = row[1].trim();
                int duration = Integer.parseInt(row[2].trim());

                int id = db.addService(masterId, name, duration);
                System.out.println("Импортирована услуга: " + name + " (id=" + id + ")");
            }

            System.out.println("Импорт завершён. Всего: " + (rows.size() - 1));
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

}