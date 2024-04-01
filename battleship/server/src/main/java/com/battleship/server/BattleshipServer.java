package com.battleship.server;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BattleshipServer extends WebSocketServer {
    private final ArrayList<GameSession> gameSessions;
    private final Map<WebSocket, String> activeSockets;

    public BattleshipServer(InetSocketAddress address) {
        super(address);
        gameSessions = new ArrayList<>();
        activeSockets = new HashMap<>();
    }

    public GameSession getGameSessionByPlayerUsername(String playerUsername) {
        for (GameSession session : this.gameSessions) {
            if (session.getPlayerOneUsername().equals(playerUsername) || session.getPlayerTwoUsername().equals(playerUsername)) {
                return session;
            }
        }
        return null;
    }

    public WebSocket getSocketByPlayerUsername(String playerUsername) {
        for (Map.Entry<WebSocket, String> entry : this.activeSockets.entrySet()) {
            if (entry.getValue().equals(playerUsername)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void onStart() {
        System.out.println("Battleship server is now running.");
    }

    @Override
    public void onOpen(WebSocket websocket, ClientHandshake clientHandshake) {
        String basicAuthString = new String(Base64.getDecoder().decode(websocket.getResourceDescriptor().substring(1)));
        String playerUsername = Arrays.asList(basicAuthString.split(":")).get(0);

        Config config = new Config();
        String basicAuth = websocket.getResourceDescriptor().substring(1);

        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(config.PlatoServerUrl + "/players/" + playerUsername);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Authorization", "Basic " + basicAuth);

            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.close();

            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Cannot authenticate " + playerUsername + " with Plato server.");
                websocket.close();
                return;
            }
        } catch (Exception e) {
            System.out.println("Cannot authenticate " + playerUsername + " with Plato server.");
            websocket.close();
            return;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        System.out.println("Player " + playerUsername + " is now connected.");
        this.activeSockets.put(websocket, playerUsername);
        websocket.send("connected");
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        if (this.activeSockets.get(webSocket) == null) {
            return;
        }

        GameSession gameSession = this.getGameSessionByPlayerUsername(this.activeSockets.get(webSocket));
        if (gameSession == null) {
            if (this.activeSockets.get(webSocket) != null) {
                System.out.println("Closed " + this.activeSockets.get(webSocket) + "'s web socket.");
                this.activeSockets.remove(webSocket);
            }
        } else {
            WebSocket playerOneSocket = this.getSocketByPlayerUsername(gameSession.getPlayerOneUsername());
            WebSocket playerTwoSocket = this.getSocketByPlayerUsername(gameSession.getPlayerTwoUsername());

            System.out.println("Closed game session " + gameSession.getId() + ".");
            this.gameSessions.remove(gameSession);

            if (playerOneSocket != null) {
                System.out.println("Closed " + this.activeSockets.get(playerOneSocket) + "'s web socket.");
                if (playerOneSocket.isOpen()) {
                    playerOneSocket.close();
                }
                this.activeSockets.remove(playerOneSocket);
            }

            if (playerTwoSocket != null) {
                System.out.println("Closed " + this.activeSockets.get(playerTwoSocket) + "'s web socket.");
                if (playerTwoSocket.isOpen()) {
                    playerTwoSocket.close();
                }
                this.activeSockets.remove(playerTwoSocket);
            }
        }

        System.out.println("Active connections: " + activeSockets.size());
        System.out.println("Active sessions: " + gameSessions.size());
    }

    @Override
    public void onError(WebSocket webSocket, Exception ex) {
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        if (message.equals("create_session")) {
            GameSession newGameSession = new GameSession();
            newGameSession.setPlayerOneUsername(this.activeSockets.get(webSocket));
            gameSessions.add(newGameSession);

            System.out.println("Player " + this.activeSockets.get(webSocket) + " created session " + newGameSession.getId() + ".");
            webSocket.send(newGameSession.getId().toString());

        } else if (message.startsWith("join_session/")) {
            String sessionUsername = Arrays.asList(message.split("join_session/")).get(1);
            String playerUsername = this.activeSockets.get(webSocket);

            for (GameSession session : gameSessions) {
                if (session.getId().toString().equals(sessionUsername)) {
                    session.setPlayerTwoUsername(playerUsername);
                    webSocket.send("joined_session");

                    System.out.println("Player " + playerUsername + " joined session " + sessionUsername + ".");

                    getSocketByPlayerUsername(session.getPlayerOneUsername()).send("send_ship_layout");
                    getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("send_ship_layout");
                    return;
                }
            }
            webSocket.send("session_not_found");

        } else if (message.startsWith("ship_layout/")) {
            String shipLayoutString = Arrays.asList(message.split("ship_layout/")).get(1);
            JSONArray shipLayout = (JSONArray) JSONValue.parse(shipLayoutString);

            String playerUsername = this.activeSockets.get(webSocket);
            GameSession session = getGameSessionByPlayerUsername(playerUsername);

            ArrayList<Ship> targetShips;

            if (session.getPlayerOneUsername().equals(playerUsername)) {
                targetShips = session.getPlayerOneShips();
            } else {
                targetShips = session.getPlayerTwoShips();
            }

            targetShips.clear();
            for (Object ship : shipLayout) {
                JSONObject shipJson = (JSONObject) ship;

                int x = ((Long) ((JSONObject) shipJson.get("bottom_left")).get("x")).intValue();
                int y = ((Long) ((JSONObject) shipJson.get("bottom_left")).get("y")).intValue();
                Point bottomLeft = new Point(x, y);

                x = ((Long) ((JSONObject) shipJson.get("top_right")).get("x")).intValue();
                y = ((Long) ((JSONObject) shipJson.get("top_right")).get("y")).intValue();
                Point topRight = new Point(x, y);

                if (Utils.isPointInsideRectangle(bottomLeft, new Point(0, 0), new Point(9, 9)) &&
                        Utils.isPointInsideRectangle(topRight, new Point(0, 0), new Point(9, 9))) {
                    Utils.normalizeRectanglePoints(bottomLeft, topRight);
                    String dimensions = Utils.getRectangleDimensions(bottomLeft, topRight);
                    int i = shipLayout.indexOf(ship);
                    if ((i == 0 && dimensions.equals("2x1")) ||
                            (i == 1 && dimensions.equals("2x1")) ||
                            (i == 2 && dimensions.equals("3x1")) ||
                            (i == 3 && dimensions.equals("4x1")) ||
                            (i == 4 && dimensions.equals("5x1")) ||
                            (i == 5 && dimensions.equals("5x2"))) {
                        Ship newShip = new Ship(bottomLeft, topRight);
                        targetShips.add(newShip);
                        continue;
                    }
                }

                targetShips.clear();
                webSocket.send("wrong_ship_layout");
                return;
            }

            // Check if ships collide each other.
            boolean hasCollisions = false;

            for (int i = 0; i <= targetShips.size() - 2; i++) {
                for (int j = i + 1; j <= targetShips.size() - 1; j++) {
                    if (Utils.doOverlap(targetShips.get(i).getBottomLeft(),
                            targetShips.get(i).getTopRight(),
                            targetShips.get(j).getBottomLeft(),
                            targetShips.get(j).getTopRight())) {
                        hasCollisions = true;
                        break;
                    }
                }
            }

            if (hasCollisions) {
                targetShips.clear();
                webSocket.send("wrong_ship_layout");
                return;
            }

            webSocket.send("ship_layout_saved");

            // TODO: REVERT
            if (session.getPlayerOneShips().size() == 1 && session.getPlayerTwoShips().size() == 1) {
                session.setWhoPlaysNext(Player.PLAYER_ONE);
                getSocketByPlayerUsername(session.getPlayerOneUsername()).send("play");
                getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("wait");
            }

        } else if (message.startsWith("fire_at/")) {
            String playerUsername = this.activeSockets.get(webSocket);
            GameSession session = getGameSessionByPlayerUsername(playerUsername);

            if (session.getPlayerOneUsername().equals(playerUsername)) {
                if (session.getWhoPlaysNext() != Player.PLAYER_ONE) {
                    webSocket.send("not_your_turn");
                    return;
                }
            } else {
                if (session.getWhoPlaysNext() != Player.PLAYER_TWO) {
                    webSocket.send("not_your_turn");
                    return;
                }
            }

            String positionString = Arrays.asList(message.split("fire_at/")).get(1);
            JSONObject positionJson = (JSONObject) JSONValue.parse(positionString);
            int x = ((Long) positionJson.get("x")).intValue();
            int y = ((Long) positionJson.get("y")).intValue();

            ArrayList<Ship> targetShips;

            if (session.getPlayerOneUsername().equals(playerUsername)) {
                targetShips = session.getPlayerTwoShips();
            } else {
                targetShips = session.getPlayerOneShips();
            }

            for (Ship ship : targetShips) {
                if (ship.getShot(new Point(x, y))) {
                    getSocketByPlayerUsername(session.getPlayerOneUsername()).send("hit/" + positionString);
                    getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("hit/" + positionString);

                    if (ship.isShotDown()) {
                        boolean allShipsAreDown = true;
                        for (Ship temp : targetShips) {
                            if (!temp.isShotDown()) {
                                allShipsAreDown = false;
                                break;
                            }
                        }

                        if (allShipsAreDown) {
                            String winnerId = "";
                            String loserId = "";
                            if (session.getPlayerOneUsername().equals(playerUsername)) {
                                winnerId = session.getPlayerOneUsername();
                                loserId = session.getPlayerTwoUsername();
                                getSocketByPlayerUsername(session.getPlayerOneUsername()).send("you_won");
                                getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("you_lost");
                            } else {
                                winnerId = session.getPlayerTwoUsername();
                                loserId = session.getPlayerOneUsername();
                                getSocketByPlayerUsername(session.getPlayerOneUsername()).send("you_lost");
                                getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("you_won");
                            }

                            getSocketByPlayerUsername(session.getPlayerOneUsername()).close();

                            // Send match result to Plato.
                            Config config = new Config();
                            HttpURLConnection httpURLConnection = null;
                            try {
                                URL url = new URL(config.PlatoServerUrl + "/matches/add");
                                httpURLConnection = (HttpURLConnection) url.openConnection();

                                httpURLConnection.setRequestProperty ("Authorization", "GameServer");
                                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setUseCaches(false);
                                httpURLConnection.setDoOutput(true);

                                String match = "{\"gameName\":\"battleship.jar\",\"loserId\":\"" + loserId + "\",\"winnerId\":\"" + winnerId + "\"}";

                                httpURLConnection.setFixedLengthStreamingMode(match.length());
                                httpURLConnection.connect();

                                try(OutputStream os = httpURLConnection.getOutputStream()) {
                                    os.write(match.getBytes());
                                }
                            } catch (Exception e) {
                                System.out.println("Cannot send match result to Plato.");
                                return;
                            } finally {
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                            }
                            return;
                        }
                    }

                    session.toggleTurn();
                    if (session.getWhoPlaysNext() == Player.PLAYER_ONE) {
                        getSocketByPlayerUsername(session.getPlayerOneUsername()).send("play");
                        getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("wait");
                    } else if (session.getWhoPlaysNext() == Player.PLAYER_TWO) {
                        getSocketByPlayerUsername(session.getPlayerOneUsername()).send("wait");
                        getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("play");
                    }
                    return;
                }
            }
            getSocketByPlayerUsername(session.getPlayerOneUsername()).send("miss/" + positionString);
            getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("miss/" + positionString);

            session.toggleTurn();
            if (session.getWhoPlaysNext() == Player.PLAYER_ONE) {
                getSocketByPlayerUsername(session.getPlayerOneUsername()).send("play");
                getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("wait");
            } else if (session.getWhoPlaysNext() == Player.PLAYER_TWO) {
                getSocketByPlayerUsername(session.getPlayerOneUsername()).send("wait");
                getSocketByPlayerUsername(session.getPlayerTwoUsername()).send("play");
            }
        }
    }

    public static void main(String[] args) {
        Config config = new Config();
        WebSocketServer server = new BattleshipServer(new InetSocketAddress("127.0.0.1", Integer.parseInt(config.BattleShipServerPort)));
        server.setConnectionLostTimeout(Integer.MAX_VALUE);
        server.run();
    }
}
