package eyesatop.controller.djinew;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.tasks.takeoff.DroneDisconnectedException;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by einav on 03/05/2017.
 * listen to connectivity, and starts thread or kills it when detecting connection.
 * inside the thread : waiting for the drone to be flying and not in ONE mode. enables it.
 * prevent time outs : while flying and in ONE mode, every 10 sec check if sticks are enabled.
 *                     if not in ONE mode,
 */

public class DjiEnableSticks {

    private final ControllerDjiNew controller;

    private final BooleanProperty sticksEnabled;

    private final ObservableBoolean hasNoEnableSticksBlockers;

    private EnableSticksThread enableSticksThread;

    private class EnableSticksThread extends Thread {

        @Override
        public void run() {

            try {
                MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Started Enable sticks thread, waiting for no blockers progressState. Current Value : " + hasNoEnableSticksBlockers.value());
                hasNoEnableSticksBlockers.awaitTrue();
                MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"No enable sticks blockers detected.");
                firstEnable();
                preventTimeOuts();
            } catch (InterruptedException e) {
                MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Killed Enable sticks thread");
            } catch (DroneDisconnectedException e) {
                e.printStackTrace();
                MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Exit Enable sticks thread : no flight controller");
            }
        }

        private void preventTimeOuts() throws InterruptedException, DroneDisconnectedException {

            MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Entered prevent timeouts.");

            while(true){

                MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Prevent time outs waiting for no enable sticks blockers. Current Value : " + hasNoEnableSticksBlockers.value());
                hasNoEnableSticksBlockers.awaitTrue();

                while(hasNoEnableSticksBlockers.value()){

                    final CountDownLatch getLatch = new CountDownLatch(1);

                    getDjiFlightController().getVirtualStickModeEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Prevent time outs Dji Result for get, Current SticksEnabled : " + aBoolean);
                            sticksEnabled.set(aBoolean);
                            getLatch.countDown();
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                            MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Prevent time outs Dji Result for get, Error : " + (djiError == null ? "NULL" : djiError.getDescription()));
                            getLatch.countDown();
                        }
                    });

                    MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Prevent time outs Waiting for Dji to finish getVirtualStickModeEnable");
                    getLatch.await();

                    if(!sticksEnabled.value()){

                        MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Prevent time outs Detected that sticks are not enabled");
                        final CountDownLatch setLatch = new CountDownLatch(1);
                        getDjiFlightController().setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,
                                        "Prevent time setVirtualStickModeEnable result : " + (djiError == null ? "Success" : "Failed," + djiError.getDescription()));
                                sticksEnabled.set(djiError == null);

                                try {
                                    getDjiFlightController().setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                                    getDjiFlightController().setVerticalControlMode(VerticalControlMode.VELOCITY);
                                    getDjiFlightController().setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                                    getDjiFlightController().setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);
                                }
                                catch (DroneDisconnectedException e){
                                }
                                setLatch.countDown();
                            }
                        });

                        setLatch.await();
                    }

                    Thread.sleep(10000);
                }
            }
        }

        private void firstEnable() throws InterruptedException, DroneDisconnectedException {

            final CountDownLatch latch = new CountDownLatch(1);

            getDjiFlightController().setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                    MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Done Dji Sticks First enable. Reasult : " + (djiError == null ? "Success" : "Failed, "+djiError.getDescription()));


                    try {
                        getDjiFlightController().setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                        getDjiFlightController().setVerticalControlMode(VerticalControlMode.VELOCITY);
                        getDjiFlightController().setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                        getDjiFlightController().setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);
                    } catch (DroneDisconnectedException e) {
                        e.printStackTrace();
                    }

                    sticksEnabled.set(djiError == null);
                    latch.countDown();
                }
            });

            MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"First Enable waiting for Dji to finish.");
            latch.await();
        }
    }


    public DjiEnableSticks(ControllerDjiNew droneController) {

        this.controller = droneController;

        hasNoEnableSticksBlockers = droneController.flying().and(droneController.rcInFunctionMode()).and(droneController.flightMode().equalsTo(FlightMode.APP_CONTROL));
        this.sticksEnabled = controller.flightTasks().getSticksEnabled();

        droneController.connectivity().observe(new Observer<DroneConnectivity>() {
            @Override
            public void observe(DroneConnectivity oldValue, DroneConnectivity newValue, Observation<DroneConnectivity> observation) {

                MainLogger.logger.write_message(LoggerTypes.ENABLE_STICKS,"Detected Connectivity Change, New Value : " + (newValue == null ? "NULL" : newValue.name()));

                if(enableSticksThread != null){
                    try {
                        enableSticksThread.interrupt();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if(newValue == DroneConnectivity.DRONE_CONNECTED){
                    enableSticksThread = new EnableSticksThread();
                    enableSticksThread.start();
                }
            }
        }).observeCurrentValue();
    }

    private FlightController getDjiFlightController() throws DroneDisconnectedException {

        FlightController djiFlightController = controller.getHardwareManager().getDjiFlightController();

        if(djiFlightController == null || !djiFlightController.isConnected()){
            throw new DroneDisconnectedException("Enable sticks");
        }
        return djiFlightController;
    }

    public BooleanProperty getSticksEnabled() {
        return sticksEnabled;
    }
}
