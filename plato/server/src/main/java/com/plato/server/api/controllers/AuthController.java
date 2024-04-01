package com.plato.server.api.controllers;

import com.plato.server.core.services.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Base64;

public class AuthController implements HttpHandler {
    private final AuthService authService;
    private final HttpHandler httpHandler;
    private final String allowedRole;

    public AuthController(AuthService authService, HttpHandler httpHandler, String allowedRole) {
        this.authService = authService;
        this.httpHandler = httpHandler;
        this.allowedRole = allowedRole;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String basicAuth = httpExchange.getRequestHeaders().get("Authorization").get(0);
            basicAuth = basicAuth.split("Basic ")[1];

            String credentials = new String(Base64.getDecoder().decode(basicAuth.getBytes()));
            String username = credentials.split(":")[0];
            String password = credentials.split(":")[1];

            String role = authService.validateAuth(username, password);

            if (role != null) {
                httpExchange.getResponseHeaders().set("Plato-Username", username);
                if (allowedRole.equals("both") || role.equals(allowedRole)) {
                    httpHandler.handle(httpExchange);
                    return;
                } else {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, 0);
                    OutputStream responseBody = httpExchange.getResponseBody();
                    responseBody.close();
                }
                return;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.close();
    }
}
