package com.battleship.client;

public class Config {
    public final String PlatoServerUrl;
    public final String BattleShipServerUrl;

    public Config() {
        this.PlatoServerUrl = System.getenv().getOrDefault("PLATO_SERVER_URL", "http://127.0.0.1:9000");
        this.BattleShipServerUrl = System.getenv().getOrDefault("BATTLESHIP_SERVER_URL", "ws://127.0.0.1:9001");
    }
}
