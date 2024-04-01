package com.plato.server.core.services;

import com.plato.server.core.models.Admin;
import com.plato.server.core.models.Player;

public class AuthService {
    private final AdminService adminService;
    private final PlayerService playerService;

    public AuthService(AdminService adminService, PlayerService playerService) {
        this.adminService = adminService;
        this.playerService = playerService;
    }

    public String validateAuth(String username, String password) {
        Admin admin = this.adminService.getAdminByUsername(username);
        if (admin != null) {
            if (admin.getPassword().equals(password)) {
                return "admin";
            }
        }

        Player player = this.playerService.getPlayerByUsername(username);
        if (player != null) {
            if (player.getPassword().equals(password)) {
                return "player";
            }
        }
        return null;
    }
}
