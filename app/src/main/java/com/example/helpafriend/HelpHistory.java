package com.example.helpafriend;

public class HelpHistory {
    private String okuName;
    private String helpDate;
    private int points;

    public HelpHistory(String okuName, String helpDate) {
        this.okuName = okuName;
        this.helpDate = helpDate;
        this.points = points;
    }

    public String getOkuName() {
        return okuName;
    }

    public String getHelpDate() {
        return helpDate;
    }

    public int getPoints() {
        return points;
    }
}