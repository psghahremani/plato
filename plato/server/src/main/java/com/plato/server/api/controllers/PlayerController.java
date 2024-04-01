package com.plato.server.api.controllers;

import com.google.gson.Gson;
import com.plato.server.core.models.Player;
import com.plato.server.core.services.PlayerService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    public class InsertPlayerController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestBody = new String(httpExchange.getRequestBody().readAllBytes());

            Gson gson = new Gson();
            Player newPlayer = gson.fromJson(requestBody, Player.class);

            boolean inserted = playerService.insertPlayer(newPlayer);
            if (inserted) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.close();
            } else {
                String response = "{\"error\":\"Username already exists.\"}";
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, response.length());

                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.write(response.getBytes());
                responseBody.close();
            }
        }
    }

    public class EditPlayerController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String username = Arrays.asList(httpExchange.getRequestURI().toString().split("/players/edit/")).get(1);
            String requestBody = new String(httpExchange.getRequestBody().readAllBytes());

            Gson gson = new Gson();
            Player targetPlayer = playerService.getPlayerByUsername(username);

            Player newPlayer = gson.fromJson(requestBody, Player.class);
            newPlayer.setId(targetPlayer.getId());

            playerService.editPlayer(newPlayer);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.close();
        }
    }

    public class GetPlayerByUsernameController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String targetUsername = Arrays.asList(httpExchange.getRequestURI().toString().split("/players/")).get(1);
            String requesterUsername = httpExchange.getResponseHeaders().getFirst("Plato-Username");

            if (!targetUsername.equals(requesterUsername)) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.close();
            }

            Player player = playerService.getPlayerByUsername(targetUsername);
            if (player == null) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.close();
            }

            Gson gson = new Gson();
            String json = gson.toJson(player);

            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, json.length());

            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(json.getBytes());
            responseBody.close();
        }
    }
}
