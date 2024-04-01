package com.plato.server.repository;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import com.plato.server.core.models.Event;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private final MongoDatabase mongoDatabase;

    public EventRepository(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void insertEvent(Event event) {
        Gson gson = new Gson();
        Document eventDocument = Document.parse(gson.toJson(event));

        this.mongoDatabase.getCollection("events").insertOne(eventDocument);
    }

    public Event getCurrentActiveEventForGame(String associatedGame) {
        int currentTime = (int) (System.currentTimeMillis() / 1000);

        Document condition1 = new Document("associatedGame", associatedGame);
        Document condition2 = new Document("from", new Document("$lt", currentTime));
        Document condition3 = new Document("until", new Document("$gt", currentTime));

        List<Document> andStatement = new ArrayList<>();
        andStatement.add(condition1);
        andStatement.add(condition2);
        andStatement.add(condition3);

        Document query = new Document("$and", andStatement);
        Document result = this.mongoDatabase.getCollection("events").find(query).first();
        if (result == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(result.toJson(), Event.class);
    }
}
