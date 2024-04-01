package com.plato.server.api.controllers;

import com.google.gson.Gson;
import com.plato.server.core.models.Event;
import com.plato.server.core.services.EventService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    public class InsertEventController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestBody = new String(httpExchange.getRequestBody().readAllBytes());

            Gson gson = new Gson();
            Event newEvent = gson.fromJson(requestBody, Event.class);
            eventService.insertEvent(newEvent);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
            httpExchange.getResponseBody().close();
        }
    }

    public class GetCurrentActiveEventForGameController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String associatedGame = Arrays.asList(httpExchange.getRequestURI().toString().split("/events/")).get(1);

            Gson gson = new Gson();
            Event activeEvent = eventService.getCurrentActiveEventForGame(associatedGame);

            if(activeEvent == null) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                httpExchange.getResponseBody().close();
                return;
            }

            String response = gson.toJson(activeEvent);
            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.getResponseBody().close();
        }
    }
}
