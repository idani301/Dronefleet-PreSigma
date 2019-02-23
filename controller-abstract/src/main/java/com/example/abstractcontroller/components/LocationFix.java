package com.example.abstractcontroller.components;

public class LocationFix {
    private final double distance;
    private final double az;

    public LocationFix(double distance, double az) {
        this.distance = distance;
        this.az = az;
    }

    public double getDistance() {
        return distance;
    }

    public double getAz() {
        return az;
    }

    @Override
    public String toString() {
        return "Distance = " + distance +
                ", Az = " + az;
    }
}
