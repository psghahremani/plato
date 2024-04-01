package com.plato.server.core.models;

public class Event {
    private String id;
    private String associatedGame;
    private Integer from;
    private Integer until;
    private Integer scoreMultiplier;

    public Event(String id, String associatedGame, Integer from, Integer until, Integer scoreMultiplier) {
        this.id = id;
        this.associatedGame = associatedGame;
        this.from = from;
        this.until = until;
        this.scoreMultiplier = scoreMultiplier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssociatedGame() {
        return associatedGame;
    }

    public void setAssociatedGame(String associatedGame) {
        this.associatedGame = associatedGame;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getUntil() {
        return until;
    }

    public void setUntil(Integer until) {
        this.until = until;
    }

    public Integer getScoreMultiplier() {
        return scoreMultiplier;
    }

    public void setScoreMultiplier(Integer scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }
}
