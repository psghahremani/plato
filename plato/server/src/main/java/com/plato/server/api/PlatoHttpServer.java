package com.plato.server.api;

import com.plato.server.api.controllers.*;
import com.plato.server.core.services.PlatoService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PlatoHttpServer {
    private final HttpServer httpServer;
    private final PlatoService platoService;

    public PlatoHttpServer(String serverPort, PlatoService platoService) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(Integer.parseInt(serverPort)), 0);
        this.platoService = platoService;
    }

    public void bindRoutes() {
        // Admin:
        AdminController adminController = new AdminController(platoService.getAdminService());

        AuthController insertAdminController = new AuthController(platoService.getAuthService(), adminController.new InsertAdminController(), "admin");
        this.httpServer.createContext("/admins/register", insertAdminController);

        AuthController editAdminController = new AuthController(platoService.getAuthService(), adminController.new EditAdminController(), "admin");
        this.httpServer.createContext("/admins/edit", editAdminController);

        // Player:

        PlayerController playerController = new PlayerController(platoService.getPlayerService());

        AuthController getPlayerByUsernameController = new AuthController(platoService.getAuthService(), playerController.new GetPlayerByUsernameController(), "player");
        this.httpServer.createContext("/players", getPlayerByUsernameController);

        this.httpServer.createContext("/players/register", playerController.new InsertPlayerController());

        AuthController editPlayerController = new AuthController(platoService.getAuthService(), playerController.new EditPlayerController(), "player");
        this.httpServer.createContext("/players/edit", editPlayerController);

        // Game:

        GameController gameController = new GameController(platoService.getGameService());

        AuthController listGamesController = new AuthController(platoService.getAuthService(), gameController.new ListGamesController(), "both");
        this.httpServer.createContext("/games", listGamesController);

        AuthController getGameDetailsController = new AuthController(platoService.getAuthService(), gameController.new GetGameDetailsController(), "both");
        this.httpServer.createContext("/games/details/", getGameDetailsController);

        AuthController downloadGameController = new AuthController(platoService.getAuthService(), gameController.new DownloadGameController(), "both");
        this.httpServer.createContext("/games/download/", downloadGameController);

        // *** Matches ***

        MatchController matchController = new MatchController(platoService.getMatchService());

        this.httpServer.createContext("/matches/add", matchController.new InsertMatchController());

        AuthController getPlayerWinCountByGameNameController = new AuthController(platoService.getAuthService(), matchController.new GetPlayerWinCountByGameNameController(), "both");
        this.httpServer.createContext("/matches/win_count/", getPlayerWinCountByGameNameController);

        AuthController getPlayerScoreByGameNameController = new AuthController(platoService.getAuthService(), matchController.new GetPlayerScoreByGameNameController(), "both");
        this.httpServer.createContext("/matches/score/", getPlayerScoreByGameNameController);

        // *** Events ***

        EventController eventController = new EventController(platoService.getEventService());

        AuthController insertEventController = new AuthController(platoService.getAuthService(), eventController.new InsertEventController(), "admin");
        this.httpServer.createContext("/events/add", insertEventController);

        AuthController getCurrentActiveEventForGameController = new AuthController(platoService.getAuthService(), eventController.new GetCurrentActiveEventForGameController(), "admin");
        this.httpServer.createContext("/events/", getCurrentActiveEventForGameController);
    }

    public void startHttpServer() {
        this.httpServer.setExecutor(null);
        this.httpServer.start();
    }
}
