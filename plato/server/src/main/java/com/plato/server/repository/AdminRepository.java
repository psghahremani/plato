package com.plato.server.repository;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import com.plato.server.core.models.Admin;
import org.bson.Document;

public class AdminRepository {
    private final MongoDatabase mongoDatabase;

    public AdminRepository(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public boolean insertAdmin(Admin admin) {
        Document query = new Document("username", admin.getUsername());
        if (this.mongoDatabase.getCollection("admins").find(query).first() != null ||
                this.mongoDatabase.getCollection("players").find(query).first() != null) {
            return false;
        }

        Gson gson = new Gson();
        Document newAdminDocument = Document.parse(gson.toJson(admin));

        this.mongoDatabase.getCollection("admins").insertOne(newAdminDocument);
        return true;
    }

    public void editAdmin(Admin admin) {
        Gson gson = new Gson();
        Document newAdminDocument = Document.parse(gson.toJson(admin));

        Document query = new Document("id", admin.getId());
        this.mongoDatabase.getCollection("admins").updateOne(query, new Document("$set", newAdminDocument));
    }

    public Admin getAdminByUsername(String username) {
        Document query = new Document("username", username);
        Document result = this.mongoDatabase.getCollection("admins").find(query).first();
        if (result == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(result.toJson(), Admin.class);
    }

    public Admin getAdminByID(String id) {
        Document query = new Document("id", id);
        Document result = this.mongoDatabase.getCollection("admins").find(query).first();
        if (result == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(result.toJson(), Admin.class);
    }
}
