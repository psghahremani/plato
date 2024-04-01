package com.battleship.server;

import java.util.ArrayList;
import java.util.UUID;

enum Player {
    PLAYER_ONE,
    PLAYER_TWO
}

public class GameSession {
    private final UUID id;
    private Player whoPlaysNext;
    private String playerOneUsername, playerTwoUsername;
    private final ArrayList<Ship> playerOneShips, playerTwoShips;

    public GameSession() {
        id = UUID.randomUUID();
        whoPlaysNext = null;
        playerOneShips = new ArrayList<>();
        playerTwoShips = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public Player getWhoPlaysNext() {
        return whoPlaysNext;
    }

    public void setWhoPlaysNext(Player whoPlaysNext) {
        this.whoPlaysNext = whoPlaysNext;
    }

    public String getPlayerOneUsername() {
        return playerOneUsername;
    }

    public void setPlayerOneUsername(String playerOneUsername) {
        this.playerOneUsername = playerOneUsername;
    }

    public String getPlayerTwoUsername() {
        return playerTwoUsername;
    }

    public void setPlayerTwoUsername(String playerTwoUsername) {
        this.playerTwoUsername = playerTwoUsername;
    }

    public ArrayList<Ship> getPlayerOneShips() {
        return playerOneShips;
    }

    public ArrayList<Ship> getPlayerTwoShips() {
        return playerTwoShips;
    }

    public void toggleTurn() {
        if (this.whoPlaysNext == Player.PLAYER_ONE) {
            this.whoPlaysNext = Player.PLAYER_TWO;
        } else if (this.whoPlaysNext == Player.PLAYER_TWO) {
            this.whoPlaysNext = Player.PLAYER_ONE;
        }
    }
}
