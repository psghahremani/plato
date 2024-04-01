package com.battleship.client;

import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class BattleshipClient extends WebSocketClient {
    private final LinkedBlockingQueue<String> socketQueue;

    public BattleshipClient(URI serverURI) {
        super(serverURI);
        socketQueue = new LinkedBlockingQueue<>();
    }

    public LinkedBlockingQueue<String> getSocketQueue() {
        return socketQueue;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection to Battleship server has been terminated.");
        System.exit(1);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Cannot connect to Battleship server.");
        System.exit(1);
    }

    @Override
    public void onMessage(String message) {
        socketQueue.add(message);
    }
}
