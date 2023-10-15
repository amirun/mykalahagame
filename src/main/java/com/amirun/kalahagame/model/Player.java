package com.amirun.kalahagame.model;

public enum Player {
    PLAYER_1("1"), PLAYER_2("2");

    private String turn;

    Player(String turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return turn;
    }
}
