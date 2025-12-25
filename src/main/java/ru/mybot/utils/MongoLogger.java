package ru.mybot.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import ru.mybot.config.ConfigLoader;

import java.util.Date;

public class MongoLogger {
    private static MongoLogger INSTANCE;
    private final MongoCollection<Document> collection;

    private MongoLogger() {
        String uri = ConfigLoader.getInstance().get("db.mongo.uri");
        if (uri == null) uri = "mongodb://localhost:27017";

        MongoClient client = MongoClients.create(uri);
        this.collection = client.getDatabase("mybot_logs").getCollection("activity_logs");
    }

    public static synchronized MongoLogger get() {
        if (INSTANCE == null) INSTANCE = new MongoLogger();
        return INSTANCE;
    }

    public void log(long userId, String action, String data) {
        new Thread(() -> {
            try {
                Document doc = new Document()
                        .append("user_id", userId)
                        .append("action", action)
                        .append("data", data)
                        .append("timestamp", new Date());
                collection.insertOne(doc);
            } catch (Exception e) {
                System.err.println("Ошибка лога в Mongo: " + e.getMessage());
            }
        }).start();
    }
}
