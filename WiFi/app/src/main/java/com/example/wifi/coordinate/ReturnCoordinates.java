package com.example.wifi.coordinate;

public class ReturnCoordinates {
    private double x;
    private double y;
    private int id;

    public ReturnCoordinates(double x, double y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
