package ru.mybot;

import com.mongodb.client.*;
import org.bson.Document;
import java.util.Date;

public class MongoLogger {
    private static MongoLogger INSTANCE;

    private final MongoClient client;
    private final MongoDatabase db;
    private final MongoCollection<Document> col;

    private MongoLogger(String uri, String dbName, String collectionName) {
        client = MongoClients.create(uri);
        db = client.getDatabase(dbName);
        col = db.getCollection(collectionName);
    }

    public static synchronized void init(String uri, String dbName, String collectionName) {
        if (INSTANCE == null) {
            INSTANCE = new MongoLogger(uri, dbName, collectionName);
        }
    }

    public static MongoLogger get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("MongoLogger не инициализирован.");
        }
        return INSTANCE;
    }

    public void logMessage(long telegramId, String message) {
        Document doc = new Document()
                .append("telegram_id", telegramId)
                .append("message", message)
                .append("time", new Date());

        col.insertOne(doc);
    }
}
