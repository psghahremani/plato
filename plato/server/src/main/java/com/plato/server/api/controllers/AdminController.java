package com.plato.server.api.controllers;

import com.google.gson.Gson;
import com.plato.server.core.models.Admin;
import com.plato.server.core.services.AdminService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public class InsertAdminController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestBody = new String(httpExchange.getRequestBody().readAllBytes());

            Gson gson = new Gson();
            Admin admin = gson.fromJson(requestBody, Admin.class);

            boolean inserted = adminService.insertAdmin(admin);
            if (inserted) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.close();
            } else {
                String response = "{\"error\":\"Username already exists.\"}";
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, response.length());

                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.getResponseBody().close();
            }
        }
    }

    public class EditAdminController implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String username = Arrays.asList(httpExchange.getRequestURI().toString().split("/admins/edit/")).get(1);
            String requestBody = new String(httpExchange.getRequestBody().readAllBytes());

            Gson gson = new Gson();
            Admin targetAdmin = adminService.getAdminByUsername(username);

            Admin newAdmin = gson.fromJson(requestBody, Admin.class);
            newAdmin.setId(targetAdmin.getId());

            adminService.editAdmin(newAdmin);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.close();
        }
    }
}
