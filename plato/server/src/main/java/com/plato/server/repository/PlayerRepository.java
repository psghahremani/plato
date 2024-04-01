package com.plato.server.repository;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import com.plato.server.core.models.Player;
import org.bson.Document;

public class PlayerRepository {
    private final MongoDatabase mongoDatabase;

    public PlayerRepository(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public boolean insertPlayer(Player player) {
        Document query = new Document("username", player.getUsername());
        if (this.mongoDatabase.getCollection("players").find(query).first() != null ||
                this.mongoDatabase.getCollection("admins").find(query).first() != null) {
            return false;
        }

        Gson gson = new Gson();
        Document newPlayerDocument = Document.parse(gson.toJson(player));

        this.mongoDatabase.getCollection("players").insertOne(newPlayerDocument);
        return true;
    }

    public void editPlayer(Player player) {
        Gson gson = new Gson();
        Document newPlayerDocument = Document.parse(gson.toJson(player));

        Document query = new Document("id", player.getId());
        this.mongoDatabase.getCollection("players").updateOne(query, new Document("$set", newPlayerDocument));
    }

    public Player getPlayerByUsername(String username) {
        Document query = new Document("username", username);
        Document result = this.mongoDatabase.getCollection("players").find(query).first();
        if (result == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(result.toJson(), Player.class);
    }

    public Player getPlayerByID(String id) {
        Document query = new Document("id", id);
        Document result = this.mongoDatabase.getCollection("players").find(query).first();
        if (result == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(result.toJson(), Player.class);
    }
}
