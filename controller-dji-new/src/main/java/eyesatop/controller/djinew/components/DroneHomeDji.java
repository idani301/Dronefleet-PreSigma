package eyesatop.controller.djinew.components;

import com.example.abstractcontroller.components.AbstractDroneHome;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.djinew.tasks.home.DjiSetHomeLocation;
import eyesatop.controller.djinew.tasks.home.DjiSetLimitationEnabled;
import eyesatop.controller.djinew.tasks.home.DjiSetMaxAltitudeFromHomeLocation;
import eyesatop.controller.djinew.tasks.home.DjiSetMaxDistanceFromHome;
import eyesatop.controller.djinew.tasks.home.DjiSetReturnHomeAltitude;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;

/**
 * Created by Idan on 09/09/2017.
 */

public class DroneHomeDji extends AbstractDroneHome {

    private final ControllerDjiNew controller;

    public DroneHomeDji(ControllerDjiNew controller){
        super();
        this.controller = controller;
    }

    @Override
    protected RunnableDroneTask<HomeTaskType> stubToRunnable(StubDroneTask<HomeTaskType> stubDroneTask) throws DroneTaskException {
        switch (stubDroneTask.taskType()){

            case SET_HOME_LOCATION:
                SetHomeLocation setHomeLocation = (SetHomeLocation)stubDroneTask;
                return new DjiSetHomeLocation(controller,setHomeLocation.location());
            case SET_LIMITATION_ENABLED:
                SetLimitationEnabled setLimitationEnabled = (SetLimitationEnabled) stubDroneTask;
                return new DjiSetLimitationEnabled(controller,setLimitationEnabled.enabled());
            case SET_MAX_DISTANCE_FROM_HOME:
                SetMaxDistanceFromHome setMaxDistanceFromHome = (SetMaxDistanceFromHome)stubDroneTask;
                return new DjiSetMaxDistanceFromHome(controller,setMaxDistanceFromHome.maxDistanceFromHome());
            case SET_MAX_ALT_FROM_TAKE_OFF_ALT:
                SetMaxAltitudeFromHomeLocation setMaxAlt = (SetMaxAltitudeFromHomeLocation)stubDroneTask;
                return new DjiSetMaxAltitudeFromHomeLocation(controller,setMaxAlt.altitude());
            case SET_RETURN_HOME_ALT:
                SetReturnHomeAltitude setReturnHomeAltitude = (SetReturnHomeAltitude)stubDroneTask;
                return new DjiSetReturnHomeAltitude(controller,setReturnHomeAltitude.altitude());
            default:
                throw new DroneTaskException("Not implemeneted : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {

    }

    @Override
    public void onComponentConnected() {
        FlightController flightController = controller.getHardwareManager().getDjiFlightController();

        if(flightController == null){
            return;
        }

        try {

            flightController.getMaxFlightRadiusLimitationEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    limitationActive().set(aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    limitationActive().set(null);
                }
            });

            flightController.getMaxFlightHeight(
                    new CommonCallbacks.CompletionCallbackWith<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            maxAltitudeFromTakeOffLocation().set((double)integer);
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                            maxAltitudeFromTakeOffLocation().set(null);
                        }
                    });

            flightController.getMaxFlightRadius(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    maxDistanceFromHome().set((double)integer);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    maxDistanceFromHome().set(null);
                }
            });

            flightController.getGoHomeHeightInMeters(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    returnHomeAltitude().set((double)integer);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    returnHomeAltitude().set(null);
                }
            });

        }
        catch (Exception e){
            MainLogger.logger.writeError(LoggerTypes.ERROR,e);
            MainLogger.logger.write_message(LoggerTypes.ERROR,"Error inside refreshLimitationInfo : " + e.getMessage());
        }
    }

}
