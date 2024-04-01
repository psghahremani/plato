package com.plato.server.repository;

import com.google.gson.Gson;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.plato.server.core.models.Match;
import org.bson.Document;

public class MatchRepository {
    private final MongoDatabase mongoDatabase;

    public MatchRepository(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void insertMatch(Match match) {
        Gson gson = new Gson();
        Document matchDocument = Document.parse(gson.toJson(match));
        this.mongoDatabase.getCollection("matches").insertOne(matchDocument);
    }

    public long getPlayerWinCountByGameName(String playerId, String gameName) {
        Document query = new Document();
        query.put("gameName", gameName);
        query.put("winnerId", playerId);

        return this.mongoDatabase.getCollection("matches").count(query);
    }

    public int getPlayerScoreByGameName(String playerId, String gameName) {
        Document query = new Document();
        query.put("gameName", gameName);
        query.put("winnerId", playerId);

        int totalScore = 0;
        MongoCursor mongoCursor = this.mongoDatabase.getCollection("matches").find(query).cursor();
        while (mongoCursor.hasNext()) {
            Document match = (Document) mongoCursor.next();
            totalScore += match.getInteger("score");
        }
        return totalScore;
    }
}
