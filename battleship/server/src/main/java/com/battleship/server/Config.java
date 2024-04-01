package com.battleship.server;

public class Config {
    public final String PlatoServerUrl;
    public final String BattleShipServerPort;

    public Config() {
        this.PlatoServerUrl = System.getenv().getOrDefault("PLATO_SERVER_URL", "http://127.0.0.1:9000");
        this.BattleShipServerPort = System.getenv().getOrDefault("BATTLESHIP_SERVER_PORT", "9001");
    }
}
