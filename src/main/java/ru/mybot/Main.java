package ru.mybot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        MongoLogger.init("mongodb://localhost:27017", "mybot_mongo", "logs");
        try {
            String TOKEN = "YOUR_BOT_TOKEN";

            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new Bot(TOKEN));

            System.out.println("Бот запущен!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
