package com.battleship.client;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scene {
    private final BattleshipClient battleshipClient;

    public Scene(BattleshipClient battleshipClient) {
        this.battleshipClient = battleshipClient;
    }

    private String readLine() {
        String command = null;
        BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            command = stdinReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return command;
    }

    private void splitScreen() {
        System.out.println("--------------------------------------------------------------------------------------");
    }

    public String initSession() {
        this.splitScreen();

        System.out.println("Do you want to create a session or join one?");
        System.out.println("* Available commands:");
        System.out.println("* create");
        System.out.println("* join <session-id>");
        System.out.print("> ");

        String command = this.readLine();
        if (command == null) {
            return this.initSession();
        }

        if (command.equals("create")) {
            this.battleshipClient.send("create_session");
            try {
                return this.battleshipClient.getSocketQueue().take();
            } catch (Exception e) {
                return this.initSession();
            }
        } else if (command.startsWith("join")) {
            List<String> commandParts = Arrays.asList(command.split(" "));
            if (commandParts.size() != 2) {
                return this.initSession();
            }

            String sessionID = commandParts.get(1);
            this.battleshipClient.send("join_session/" + sessionID);

            try {
                String response = this.battleshipClient.getSocketQueue().take();
                if (!response.equals("joined_session")) {
                    return this.initSession();
                }
                return sessionID;
            } catch (Exception e) {
                return this.initSession();
            }
        } else {
            return this.initSession();
        }
    }

    public void chooseShipLayout() {
        this.splitScreen();
        System.out.println("Choose your ship layout.");
        System.out.println("* Available commands:");
        System.out.println("* ship <bl_x> <bl_xy> <tr_x> <tr_y>");
        System.out.print("> ");

        ArrayList<String> shipJsonStrings = new ArrayList<>();

        String command = this.readLine();
        if (command == null) {
            this.chooseShipLayout();
            return;
        }

        List<String> shipCommands = Arrays.asList(command.split("/"));
        // TODO: REVERT
        if(shipCommands.size() != 1) {
            this.chooseShipLayout();
            return;
        }

        // TODO: REVERT
        for (int i = 0; i < 1; i++) {
            command = shipCommands.get(i);
            if (command.startsWith("ship")) {
                List<String> commandParts = Arrays.asList(command.split(" "));
                if (commandParts.size() != 5) {
                    this.chooseShipLayout();
                    return;
                }
                Point bottomLeft = new Point(), topRight = new Point();

                bottomLeft.x = Integer.parseInt(commandParts.get(1));
                bottomLeft.y = Integer.parseInt(commandParts.get(2));

                topRight.x = Integer.parseInt(commandParts.get(3));
                topRight.y = Integer.parseInt(commandParts.get(4));

                String jsonString = String.format(
                        "{\"bottom_left\":{\"x\":%d,\"y\":%d},\"top_right\":{\"x\":%d,\"y\":%d}}",
                        bottomLeft.x, bottomLeft.y,
                        topRight.x, topRight.y);
                shipJsonStrings.add(jsonString);
            } else {
                this.chooseShipLayout();
            }
        }
        StringBuilder shipsLayout = new StringBuilder("[");
        for (String shipJson : shipJsonStrings) {
            shipsLayout.append(shipJson).append(",");
        }

        shipsLayout.setLength(shipsLayout.length() - 1);
        shipsLayout.append("]");

        this.battleshipClient.send("ship_layout/" + shipsLayout.toString());
        try {
            String response = this.battleshipClient.getSocketQueue().take();
            if (!response.equals("ship_layout_saved")) {
                this.chooseShipLayout();
            }
        } catch (Exception e) {
            this.chooseShipLayout();
        }
    }

    public void play() {
        this.splitScreen();

        System.out.println("Choose where to shoot.");
        System.out.println("* Available commands:");
        System.out.println("* shoot x y");
        System.out.print("> ");

        String command = this.readLine();
        if (command == null) {
            this.play();
            return;
        }

        if (command.startsWith("shoot")) {
            List<String> commandParts = Arrays.asList(command.split(" "));
            if (commandParts.size() != 3) {
                this.play();
                return;
            }

            Point position = new Point();
            position.x = Integer.parseInt(commandParts.get(1));
            position.y = Integer.parseInt(commandParts.get(2));

            String jsonString = String.format(
                    "{\"x\":%d,\"y\":%d}",
                    position.x, position.y);

            this.battleshipClient.send("fire_at/" + jsonString);
        } else {
            this.play();
        }
    }
}
