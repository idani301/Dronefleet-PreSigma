package eyesatop.controller.djinew.components;

import com.example.abstractcontroller.components.AbstractDroneRemoteController;

import dji.common.remotecontroller.ChargeRemaining;
import dji.common.remotecontroller.GPSData;
import dji.common.remotecontroller.HardwareState;
import eyesatop.controller.beans.BatteryState;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.RCFlightModeSwitchPosition;
import eyesatop.controller.beans.SingleStickPosition;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.remotecontroller.RemoteControllerTaskType;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import logs.LoggerTypes;

/**
 * Created by Idan on 12/09/2017.
 */

public class DroneRemoteControllerDji extends AbstractDroneRemoteController {

    private final ControllerDjiNew controller;
    private boolean everStartedCallbacks = false;
    private static final int DJI_MAX_STICK = 660;

    public DroneRemoteControllerDji(ControllerDjiNew controller) {

        this.controller = controller;
        startUpdateRcInFunctionModeCalcs();
    }

    @Override
    protected RunnableDroneTask<RemoteControllerTaskType> stubToRunnable(StubDroneTask<RemoteControllerTaskType> stubDroneTask) throws DroneTaskException {
        switch (stubDroneTask.taskType()){
            default:
                throw new DroneTaskException("Not implemented : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {

        if(everStartedCallbacks){
            return;
        }
        everStartedCallbacks = true;

        controller.getHardwareManager().getDjiRemoteController().setHardwareStateCallback(new HardwareState.HardwareStateCallback() {
            @Override
            public void onUpdate(HardwareState hardwareState) {

                RCFlightModeSwitchPosition currentState = fromDjiSwitchPosition(hardwareState.getFlightModeSwitch());
                getRcFlightModeSwitchPosition().setIfNew(currentState);
                SticksPosition currentPosition = new SticksPosition(
                        new SingleStickPosition(fromDjiToPercent(hardwareState.getRightStick().getVerticalPosition())),
                        new SingleStickPosition(fromDjiToPercent(hardwareState.getRightStick().getHorizontalPosition())),
                        new SingleStickPosition(fromDjiToPercent(hardwareState.getLeftStick().getVerticalPosition())),
                        new SingleStickPosition(fromDjiToPercent(hardwareState.getLeftStick().getHorizontalPosition()))
                );
                getSticksPosition().set(currentPosition);
                getGoHomeButtonPressed().set(hardwareState.getGoHomeButton().isClicked());
            }
        });

        controller.getHardwareManager().getDjiRemoteController().setChargeRemainingCallback(new ChargeRemaining.Callback() {
            @Override
            public void onUpdate(ChargeRemaining chargeRemaining) {
                getRcBattery().setIfNew(new BatteryState(100,chargeRemaining.getRemainingChargeInPercent(),-1,-1));
            }
        });

        controller.getHardwareManager().getDjiRemoteController().setGPSDataCallback(new GPSData.Callback() {
            @Override
            public void onUpdate(GPSData gpsData) {

                MainLogger.logger.write_message(LoggerTypes.RC_GPS,"Got gps data : " + gpsData.getLocation() + "," + gpsData.getLocation().getLongitude());

                Location rcNewLocation = new Location(gpsData.getLocation().getLatitude(),gpsData.getLocation().getLongitude());
                getRcLocation().setIfNew(rcNewLocation);
            }
        });
    }

    public RCFlightModeSwitchPosition fromDjiSwitchPosition(HardwareState.FlightModeSwitch state){

        switch (state){

            case POSITION_ONE:
                return RCFlightModeSwitchPosition.ONE;
            case POSITION_TWO:
                return RCFlightModeSwitchPosition.TWO;
            case POSITION_THREE:
                return RCFlightModeSwitchPosition.THREE;
        }
        return RCFlightModeSwitchPosition.UNKNOWN;
    }


    private void startUpdateRcInFunctionModeCalcs(){
        controller.model().observe(new Observer<DroneModel>() {
            @Override
            public void observe(DroneModel oldValue, DroneModel newValue, Observation<DroneModel> observation) {
                updateRcInFunctionMode();
            }
        });

        getRcFlightModeSwitchPosition().observe(new Observer<RCFlightModeSwitchPosition>() {
            @Override
            public void observe(RCFlightModeSwitchPosition oldValue, RCFlightModeSwitchPosition newValue, Observation<RCFlightModeSwitchPosition> observation) {
                updateRcInFunctionMode();
            }
        });
    }

    private void updateRcInFunctionMode(){
        if(controller.model().isNull() || getRcFlightModeSwitchPosition().isNull()){
            getRcInFunctionMode().setIfNew(null);
        }
        else{
            getRcInFunctionMode().setIfNew(getRcFlightModeSwitchPosition().value().isFunctionMode(controller.model().value()));
        }
    }

    @Override
    public void onComponentConnected() {

    }

    private final double fromDjiToPercent(int valueOfDji){
        return 100*(double)valueOfDji/DJI_MAX_STICK;
    }
}
