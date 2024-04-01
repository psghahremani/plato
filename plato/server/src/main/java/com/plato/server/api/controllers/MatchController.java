package com.plato.server.api.controllers;

import com.google.gson.Gson;
import com.plato.server.core.models.Match;
import com.plato.server.core.services.MatchService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    public class InsertMatchController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String token = httpExchange.getRequestHeaders().getFirst("Authorization");
            if (!token.equals("GameServer")) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
                httpExchange.getResponseBody().close();
            }

            Gson gson = new Gson();
            String requestBody = new String(httpExchange.getRequestBody().readAllBytes());

            Match match = gson.fromJson(requestBody, Match.class);
            matchService.insertMatch(match);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
            httpExchange.getResponseBody().close();
        }
    }

    public class GetPlayerWinCountByGameNameController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<String> urlParts = Arrays.asList(httpExchange.getRequestURI().toString().split("/matches/win_count/"));
            urlParts = Arrays.asList(urlParts.get(1).split("/"));
            String gameName = urlParts.get(0);
            String playerId = urlParts.get(1);

            long count = matchService.getPlayerWinCountByGameName(playerId, gameName);

            String response = "{\"count\":" + count + "}";

            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.getResponseBody().close();
        }
    }

    public class GetPlayerScoreByGameNameController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<String> urlParts = Arrays.asList(httpExchange.getRequestURI().toString().split("/matches/score/"));
            urlParts = Arrays.asList(urlParts.get(1).split("/"));
            String gameName = urlParts.get(0);
            String playerId = urlParts.get(1);

            long score = matchService.getPlayerScoreByGameName(playerId, gameName);

            String response = "{\"score\":" + score + "}";

            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.getResponseBody().close();
        }
    }
}
