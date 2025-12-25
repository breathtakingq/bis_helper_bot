package ru.mybot.config;

import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static ConfigLoader INSTANCE;
    private Properties properties;

    private ConfigLoader() {
        properties = new Properties();
        try (java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("environment.properties")) {
            if (input == null) {
                System.out.println("Невозможно найти файл конфигурации");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
        }
    }


    public static synchronized ConfigLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigLoader();
        }
        return INSTANCE;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
