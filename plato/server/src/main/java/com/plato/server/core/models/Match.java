package com.plato.server.core.models;

public class Match {
    private String id;
    private String eventId;
    private String gameName;
    private String winnerId;
    private String loserId;
    private Integer playedAt;
    private Integer score;

    public Match(String id, String eventId, String gameName, String winnerId, String loserId, Integer playedAt, Integer score) {
        this.id = id;
        this.eventId = eventId;
        this.gameName = gameName;
        this.winnerId = winnerId;
        this.loserId = loserId;
        this.playedAt = playedAt;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getLoserId() {
        return loserId;
    }

    public void setLoserId(String loserId) {
        this.loserId = loserId;
    }

    public Integer getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Integer playedAt) {
        this.playedAt = playedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
