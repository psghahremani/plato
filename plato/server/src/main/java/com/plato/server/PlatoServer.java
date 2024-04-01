package com.plato.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.plato.server.api.PlatoHttpServer;
import com.plato.server.config.PlatoServerConfig;
import com.plato.server.core.services.*;
import com.plato.server.repository.*;

import java.io.File;

public class PlatoServer {
    public static void main(String[] args) {
        PlatoServerConfig config = new PlatoServerConfig();

        MongoClientOptions.Builder options = MongoClientOptions.builder();
        options.serverSelectionTimeout(1000);
        MongoClient mongoClient = new MongoClient(new ServerAddress(config.MongoHost, Integer.parseInt(config.MongoPort)), options.build());
        MongoDatabase mongoDatabase = mongoClient.getDatabase(config.MongoDatabaseName);

        GameRepository gameRepository = new GameRepository(mongoDatabase);
        GameService gameService = new GameService(config.GamesDirectory, gameRepository);

        boolean createdGamesDirectory = new File(config.GamesDirectory).mkdirs();
        if (createdGamesDirectory) {
            System.out.println("Created games directory.");
        } else {
            System.out.println("Using existing game directory.");
            System.out.println("Loading games from game directory.");
            gameService.updateGamesList();
        }

        AdminService adminService = new AdminService(new AdminRepository(mongoDatabase));
        PlayerService playerService = new PlayerService(new PlayerRepository(mongoDatabase));
        AuthService authService = new AuthService(adminService, playerService);
        EventService eventService = new EventService(new EventRepository(mongoDatabase));
        MatchService matchService = new MatchService(new MatchRepository(mongoDatabase), eventService);

        PlatoService platoService = new PlatoService(authService, adminService, playerService, gameService, matchService, eventService);

        PlatoHttpServer platoHttpServer;
        try {
            platoHttpServer = new PlatoHttpServer(config.HttpServerPort, platoService);
            platoHttpServer.bindRoutes();
            platoHttpServer.startHttpServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
