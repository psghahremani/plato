package com.plato.server.core.services;

public class PlatoService {
    private final AuthService authService;
    private final AdminService adminService;
    private final PlayerService playerService;
    private final GameService gameService;
    private final MatchService matchService;
    private final EventService eventService;

    public PlatoService(AuthService authService, AdminService adminService, PlayerService playerService, GameService gameService, MatchService matchService, EventService eventService) {
        this.authService = authService;
        this.adminService = adminService;
        this.playerService = playerService;
        this.gameService = gameService;
        this.matchService = matchService;
        this.eventService = eventService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public GameService getGameService() {
        return gameService;
    }

    public MatchService getMatchService() {
        return matchService;
    }

    public EventService getEventService() {
        return eventService;
    }
}
