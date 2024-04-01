package com.plato.server.config;

public final class PlatoServerConfig {
    public final String MongoHost;
    public final String MongoPort;
    public final String MongoDatabaseName;
    public final String MongoConnectionString;

    public final String GamesDirectory;
    public final String HttpServerPort;

    public PlatoServerConfig() {
        MongoHost = System.getenv().getOrDefault("MONGO_HOST", "172.17.0.2");
        MongoPort = System.getenv().getOrDefault("MONGO_PORT", "27017");
        MongoDatabaseName = System.getenv().getOrDefault("MONGO_DB_NAME", "platoDB");
        MongoConnectionString = String.format("mongodb://%s:%s", MongoHost, MongoPort);

        GamesDirectory = System.getenv().getOrDefault("PLATO_GAMES_DIR", "./plato_games");
        HttpServerPort = System.getenv().getOrDefault("PLATO_PORT", "9000");
    }
}
