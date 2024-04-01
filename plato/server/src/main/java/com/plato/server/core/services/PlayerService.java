package com.plato.server.core.services;

import com.plato.server.core.models.Player;
import com.plato.server.repository.PlayerRepository;

import java.util.UUID;

public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public boolean insertPlayer(Player player) {
        player.setId(UUID.randomUUID().toString());
        player.setRegisteredAt((int) (System.currentTimeMillis() / 1000));

        return this.playerRepository.insertPlayer(player);
    }

    public void editPlayer(Player player) {
        player.setRegisteredAt(null);

        this.playerRepository.editPlayer(player);
    }

    public Player getPlayerByUsername(String username) {
        return this.playerRepository.getPlayerByUsername(username);
    }

    public Player getPlayerById(String id) {
        return this.playerRepository.getPlayerByID(id);
    }
}
