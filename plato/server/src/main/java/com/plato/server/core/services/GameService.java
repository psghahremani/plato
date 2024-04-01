package com.plato.server.core.services;

import com.plato.server.core.models.Game;
import com.plato.server.repository.GameRepository;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class GameService {
    private final String gamesDirectory;
    private final GameRepository gameRepository;

    public GameService(String gamesDirectory, GameRepository gameRepository) {
        this.gamesDirectory = gamesDirectory;
        this.gameRepository = gameRepository;
    }

    private byte[] readFileBytes(String fileName) {
        File gameFile = new File(this.gamesDirectory + "/" + fileName);
        if (gameFile.exists() && !gameFile.isDirectory()) {
            byte[] gameBytes;
            try {
                gameBytes = Files.readAllBytes(gameFile.toPath());
                return gameBytes;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Game getGameByName(String gameName) {
        return this.gameRepository.getGameByName(gameName);
    }

    public byte[] getGameContent(String gameName) {
        return this.readFileBytes(gameName);
    }

    public void updateGamesList() {
        this.gameRepository.clearRepository();

        File gamesDirectory = new File(this.gamesDirectory);
        String[] gameFiles = gamesDirectory.list((currentFile, name) -> new File(currentFile, name).isFile());

        if (gameFiles == null) {
            return;
        }

        for (String gameFileName : gameFiles) {
            byte[] gameFileContent = this.readFileBytes(gameFileName);
            if (gameFileContent == null) {
                continue;
            }

            String fileHash = DigestUtils.sha256Hex(gameFileContent);
            int fileSize = gameFileContent.length;
            Game game = new Game(gameFileName, fileHash, fileSize);
            gameRepository.insertGame(game);
        }
    }

    public ArrayList<String> listGames() {
        return this.gameRepository.listGames();
    }
}
