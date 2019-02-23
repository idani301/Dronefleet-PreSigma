package eyesatop.controller.beans;

import eyesatop.util.drone.DroneModel;

/**
 * Created by einav on 26/04/2017.
 */
public enum RCFlightModeSwitchPosition {
    ONE,
    TWO,
    THREE,
    UNKNOWN;

    public boolean isFunctionMode(DroneModel model){

        if(model == DroneModel.MAVIC){
            return this == TWO;
        }
        else if(model == DroneModel.UNKNOWN || model == DroneModel.M_600 || model == DroneModel.NOT_SUPPORTED || model == DroneModel.PHANTOM_4){
            return this == THREE;
        }

        return this == ONE;
    }
}
