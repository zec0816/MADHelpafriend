package com.example.helpafriend;

public class LeaderboardEntry {
    private final String username;
    private final int numHelped;

    public LeaderboardEntry(String username, int numHelped) {
        this.username = username;
        this.numHelped = numHelped;
    }

    public String getUsername() {
        return username;
    }

    public int getNumHelped() {
        return numHelped;
    }

    public int getPoints() {
        return numHelped * 50;
    }
}
