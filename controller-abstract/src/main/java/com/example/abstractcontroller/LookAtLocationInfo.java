package com.example.abstractcontroller;

import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;

/**
 * Created by Idan on 24/09/2017.
 */

public class LookAtLocationInfo {

    private final Telemetry telemetry;
    private final GimbalState gimbalState;
    private final Double takeOffLocationDTM;

    public LookAtLocationInfo(Telemetry telemetry, GimbalState gimbalState, Double takeOffLocationDTM) {
        this.telemetry = telemetry;
        this.gimbalState = gimbalState;
        this.takeOffLocationDTM = takeOffLocationDTM;
    }

    public Telemetry getTelemetry() {
        return telemetry;
    }

    public GimbalState getGimbalState() {
        return gimbalState;
    }

    public boolean isNewInfo(LookAtLocationInfo newData){
        return newData == null || isTakeoffLocationDTMNew(newData.getTakeOffLocationDTM()) || isGimbalStateNew(gimbalState,newData.getGimbalState()) || isLocationNew(telemetry,newData.getTelemetry());
    }

    private boolean isTakeoffLocationDTMNew(Double newValue){
        if(takeOffLocationDTM == null){
            return newValue != null;
        }
        else if(newValue == null){
            return true;
        }

        return Double.compare(takeOffLocationDTM,newValue) != 0;
    }

    public Double getTakeOffLocationDTM() {
        return takeOffLocationDTM;
    }

    private static final double GIMBAL_MIN_DEGREE_CHANGE = 1;
    private boolean isGimbalStateNew(GimbalState oldValue, GimbalState newValue){

        if(oldValue == null){
            return newValue != null;
        }
        else if(newValue == null){
            return true;
        }

        if(Math.abs(oldValue.getPitch() - newValue.getPitch()) > GIMBAL_MIN_DEGREE_CHANGE){
            return true;
        }

        if(Math.abs(oldValue.getYaw() - newValue.getYaw()) > GIMBAL_MIN_DEGREE_CHANGE){
            return true;
        }
        return false;
    }

    private boolean isLocationNew(Telemetry oldValue,Telemetry newValue){

        if(oldValue == null){
            return newValue != null;
        }
        else if(newValue == null){
            return true;
        }

        Location oldLocation = oldValue.location();
        Location newLocation = newValue.location();

        if(oldLocation == null){
            return newLocation != null;
        }
        else if(newLocation == null){
            return true;
        }

        if(oldLocation.distance3D(newLocation) > Location.MIN_CHANGE){
            return true;
        }
        return false;
    }
}
