package com.plato.server.core.models;

public class Game {
    private String name;
    private String hash;
    private int size;

    public Game(String name, String hash, int size) {
        this.name = name;
        this.hash = hash;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
