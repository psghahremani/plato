package com.battleship.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();

        System.out.println("Enter your Plato credentials like <Username:Password>:");
        String credentials = "";

        BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            credentials = stdinReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String username = Arrays.asList(credentials.split(":")).get(0);
        String basicAuth = new String(Base64.getEncoder().encode(credentials.getBytes()));

        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(config.PlatoServerUrl + "/players/" + username);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", "Basic " + basicAuth);

            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.close();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Cannot authenticate player with Plato server.");
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        System.out.println("Welcome " + username + "!");
        System.out.println("Attempting to connect to Battleship server...");

        BattleshipClient client = null;
        try {
            client = new BattleshipClient(new URI(config.BattleShipServerUrl + "/" + basicAuth));
            client.connectBlocking();
            client.sendPing();
        } catch (Exception e) {
            System.out.println("Battleship server is down.");
            System.exit(1);
        }

        try {
            client.getSocketQueue().take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Connected to Battleship server.");

        Scene scene = new Scene(client);
        String sessionID = scene.initSession();
        System.out.println("Session id: " + sessionID);

        System.out.println("Waiting for both players to join session.");
        try {
            String message = client.getSocketQueue().take();
            if (!message.equals("send_ship_layout")) {
                System.out.println("Something went wrong.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.exit(1);
        }

        scene.chooseShipLayout();
        System.out.println("Ship layout saved.");

        while (true) {
            try {
                String message = client.getSocketQueue().take();
                switch (message) {
                    case "wait":
                        System.out.println("Wait for your opponent to take a shot.");
                        break;
                    case "play":
                        scene.play();
                        break;
                    case "you_won":
                        System.out.println("You won!");
                        System.exit(0);
                    case "you_lost":
                        System.out.println("You lost!");
                        System.exit(0);
                    default:
                        System.out.println("Something went wrong.");
                        System.exit(1);
                }

                message = client.getSocketQueue().take();
                if (message.startsWith("hit") || message.startsWith("miss")) {
                    System.out.println(message);
                } else {
                    System.out.println("Something went wrong.");
                    System.exit(1);
                }
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }
}
