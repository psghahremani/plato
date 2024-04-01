package com.plato.server.api.controllers;

import com.google.gson.Gson;
import com.plato.server.core.models.Game;
import com.plato.server.core.services.GameService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    public class ListGamesController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            ArrayList<String> gamesList = gameService.listGames();
            Gson gson = new Gson();
            String json = gson.toJson(gamesList);

            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, json.length());

            httpExchange.getResponseBody().write(json.getBytes());
            httpExchange.getResponseBody().close();
        }
    }

    public class GetGameDetailsController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String gameName = Arrays.asList(httpExchange.getRequestURI().toString().split("/games/details/")).get(1);
            Game game = gameService.getGameByName(gameName);
            if (game == null) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.close();
                return;
            }

            Gson gson = new Gson();
            String json = gson.toJson(game);

            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, json.length());

            httpExchange.getResponseBody().write(json.getBytes());
            httpExchange.getResponseBody().close();
        }
    }

    public class DownloadGameController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String gameName = Arrays.asList(httpExchange.getRequestURI().toString().split("/games/download/")).get(1);
            Game game = gameService.getGameByName(gameName);
            if (game == null) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.close();
                return;
            }

            byte[] gameContent = gameService.getGameContent(gameName);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, gameContent.length);
            httpExchange.getResponseBody().write(gameContent);
            httpExchange.getResponseBody().close();
        }
    }
}
