package com.plato.server.repository;

import com.google.gson.Gson;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.plato.server.core.models.Game;
import org.bson.Document;

import java.util.ArrayList;

public class GameRepository {
    private final MongoDatabase mongoDatabase;

    public GameRepository(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void clearRepository() {
        this.mongoDatabase.getCollection("games").drop();
    }

    public void insertGame(Game game) {
        Gson gson = new Gson();
        Document newGameDocument = Document.parse(gson.toJson(game));

        this.mongoDatabase.getCollection("games").insertOne(newGameDocument);
    }

    public Game getGameByName(String gameName) {
        Document query = new Document("name", gameName);

        Document result = this.mongoDatabase.getCollection("games").find(query).first();
        if (result == null) {
            return null;
        }

        return new Game(result.getString("name"), result.getString("hash"), result.getInteger("size"));
    }

    public ArrayList<String> listGames() {
        ArrayList<String> gamesList = new ArrayList<>();
        try (MongoCursor<Document> cursor = this.mongoDatabase.getCollection("games").find().iterator()) {
            while (cursor.hasNext()) {
                gamesList.add(cursor.next().getString("name"));
            }
        }
        return gamesList;
    }
}
