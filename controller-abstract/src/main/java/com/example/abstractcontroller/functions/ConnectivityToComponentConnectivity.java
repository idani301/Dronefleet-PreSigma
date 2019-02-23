package com.example.abstractcontroller.functions;

import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.util.Function;

/**
 * Created by Idan on 02/11/2017.
 */

public class ConnectivityToComponentConnectivity implements Function<DroneConnectivity,ComponentConnectivityType> {

    private static final ConnectivityToComponentConnectivity instance = new ConnectivityToComponentConnectivity();

    public static ConnectivityToComponentConnectivity getInstance(){
        return instance;
    }

    @Override
    public ComponentConnectivityType apply(DroneConnectivity input) {

        if(input == null){
            return ComponentConnectivityType.NULL;
        }

        switch (input){

            case NOT_CONNECTED:
                return ComponentConnectivityType.NOT_CONNECTED;
            case CONTROLLER_CONNECTED:
                return ComponentConnectivityType.NOT_CONNECTED;
            case REFRESHING:
                return ComponentConnectivityType.NOT_CONNECTED;
            case DRONE_CONNECTED:
                return ComponentConnectivityType.CONNECTED;
        }

        return ComponentConnectivityType.NULL;
    }
}
