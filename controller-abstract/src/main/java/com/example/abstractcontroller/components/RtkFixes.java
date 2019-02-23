//package com.example.abstractcontroller.components;
//
//public class RtkFixes {
//    private final Double distance;
//    private final Double az;
//    private final Double altitude;
//    private final long createdTime;
//
//    public RtkFixes(Double distance, Double az, Double altitude, long createdTime) {
//        this.distance = distance;
//        this.az = az;
//        this.altitude = altitude;
//        this.createdTime = createdTime;
//    }
//
//    public Double getDistance() {
//        return distance;
//    }
//
//    public Double getAz() {
//        return az;
//    }
//
//    public Double getAltitude() {
//        return altitude;
//    }
//
//    public long getCreatedTime() {
//        return createdTime;
//    }
//
//    public boolean isRelevant(){
//        return (createdTime - System.currentTimeMillis() < 1500);
//    }
//
//    @Override
//    public String toString() {
//        return "RtkFixes{" +
//                "distance=" + distance +
//                ", az=" + az +
//                ", altitude=" + altitude +
//                ", createdTime=" + createdTime +
//                '}';
//    }
//}
