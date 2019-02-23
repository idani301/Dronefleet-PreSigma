package eyesatop.controller.djinew;

import com.example.abstractcontroller.components.ComponentConnectivityType;

import dji.common.product.Model;
import dji.sdk.airlink.AirLink;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import dji.sdk.sdkmanager.DJISDKManager;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.util.drone.DroneModel;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;

/**
 * Created by einav on 24/01/2017.
 */

public class DjiHardwareManager {

    private Camera djiCamera                     = null;
    private Battery djiBattery                   = null;
    private FlightController djiFlightController = null;
    private RemoteController djiRemoteController = null;
    private Gimbal djiGimbal                     = null;
    private AirLink djiAirLink                   = null;

    private ComponentConnectivityType lastDjiCamera           = ComponentConnectivityType.NULL;
    private ComponentConnectivityType lastDjiBattery          = ComponentConnectivityType.NULL;
    private ComponentConnectivityType lastDjiFlightController = ComponentConnectivityType.NULL;
    private ComponentConnectivityType lastDjiRemoteController = ComponentConnectivityType.NULL;
    private ComponentConnectivityType lastDjiGimbal           = ComponentConnectivityType.NULL;
    private ComponentConnectivityType lastDjiAirLink          = ComponentConnectivityType.NULL;

    public Camera getDjiCamera() {
        return djiCamera;
    }

    public Battery getDjiBattery() {
        return djiBattery;
    }

    public FlightController getDjiFlightController() {
        return djiFlightController;
    }

    public RemoteController getDjiRemoteController() {
        return djiRemoteController;
    }

    public Gimbal getDjiGimbal() {
        return djiGimbal;
    }

    public AirLink getDjiAirLink() {
        return djiAirLink;
    }

    private ControllerDjiNew controller;

    public DjiHardwareManager(ControllerDjiNew droneController){
        this.controller = droneController;
    }

    // Gets the current product instance that connected.
    private synchronized BaseProduct getProductInstance() {
        return DJISDKManager.getInstance().getProduct();
    }

    private boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    // Returns the current product connected as an aircraft type.
    private synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) return null;
        return (Aircraft) getProductInstance();
    }

    public void hardwareChanged(){

        DroneConnectivity newState = DroneConnectivity.NOT_CONNECTED;
        DroneConnectivity oldState = controller.connectivity().value();

        boolean hasRemoteController = false;
        boolean hasDrone            = false;

        if(getAircraftInstance() != null){

            if(getAircraftInstance().getRemoteController() != null && getAircraftInstance().getRemoteController().isConnected()){
                hasRemoteController = true;
            }

            if(getAircraftInstance().getFlightController() != null && getAircraftInstance().getFlightController().isConnected()){
                hasDrone = true;
            }
        }

        if(hasRemoteController && !hasDrone){
            newState = DroneConnectivity.CONTROLLER_CONNECTED;
        }

        if(hasRemoteController && hasDrone){
            newState = DroneConnectivity.DRONE_CONNECTED;
        }

        if(!hasRemoteController && hasDrone){
            MainLogger.logger.write_message(LoggerTypes.ERROR,"Impossible Drone State");
        }

        if(getAircraftInstance() != null) {
            djiCamera           = getAircraftInstance().getCamera();
            djiBattery          = getAircraftInstance().getBattery();
            djiFlightController = getAircraftInstance().getFlightController();
            djiRemoteController = getAircraftInstance().getRemoteController();
            djiGimbal           = getAircraftInstance().getGimbal();
            djiAirLink          = getAircraftInstance().getAirLink();
        }
        else{
            djiCamera           = null;
            djiBattery          = null;
            djiFlightController = null;
            djiRemoteController = null;
            djiGimbal           = null;
            djiAirLink          = null;
        }

        if(newState != oldState){
            controller.connectivity().set(newState);
        }

        if(getAircraftInstance() == null){
            controller.model().set(DroneModel.UNKNOWN);
        }
        else{
            Model model = getAircraftInstance().getModel();
            if(model == null){
                model = Model.UNKNOWN_AIRCRAFT;
            }

            switch (model){
                case MATRICE_100:
                    controller.gimbal().fullGimbalSupported().setIfNew(true);
                    break;
                case MATRICE_600:
                    controller.gimbal().fullGimbalSupported().setIfNew(true);
                    break;
                    default:
                        controller.gimbal().fullGimbalSupported().setIfNew(false);
            }

            switch(model) {

                case PHANTOM_4_PRO_V2:
                    controller.model().setIfNew(DroneModel.PHANTOM_4);
                    break;
                case PHANTOM_4_RTK:
                    controller.model().setIfNew(DroneModel.PHANTOM_4);
                    break;
                case MAVIC_2_PRO:
                    controller.model().setIfNew(DroneModel.MAVIC_2);
                    break;
                case MAVIC_2_ENTERPRISE:
                    controller.model().setIfNew(DroneModel.MAVIC_2);
                    break;
                case MAVIC_2_ENTERPRISE_DUAL:
                    controller.model().setIfNew(DroneModel.MAVIC_2);
                    break;
                case MAVIC_2_ZOOM:
                    controller.model().setIfNew(DroneModel.MAVIC_2);
                    break;
                case MAVIC_2:
                    controller.model().setIfNew(DroneModel.MAVIC_2);
                    break;
                case MATRICE_200:
                    controller.model().setIfNew(DroneModel.MATRCIE_200);
                    break;
                case MATRICE_210:
                    controller.model().setIfNew(DroneModel.MATRCIE_200);
                    break;
                case MATRICE_210_RTK:
                    controller.model().setIfNew(DroneModel.MATRCIE_200);
                    break;
                case MATRICE_100:
                    controller.model().set(DroneModel.MATRICE100);
                    break;
                case PHANTOM_3_ADVANCED:
                    controller.model().set(DroneModel.PHANTOM_3);
                    break;
                case PHANTOM_3_PROFESSIONAL:
                    controller.model().set(DroneModel.PHANTOM_3);
                    break;
                case PHANTOM_3_STANDARD:
                    controller.model().set(DroneModel.PHANTOM_3);
                    break;
                case Phantom_3_4K:
                    controller.model().set(DroneModel.PHANTOM_3);
                    break;
                case PHANTOM_4:
                    controller.model().set(DroneModel.PHANTOM_4);
                    break;
                case PHANTOM_4_PRO:
                    controller.model().set(DroneModel.PHANTOM_4);
                    break;
                case MAVIC_PRO:
                    controller.model().set(DroneModel.MAVIC);
                    break;
                case MATRICE_600:
                    controller.model().set(DroneModel.M_600);
                    break;
                case MATRICE_600_PRO:
                    controller.model().set(DroneModel.M_600);
                    break;
                case PHANTOM_4_ADVANCED:
                    controller.model().set(DroneModel.PHANTOM_4);
                    break;
                case UNKNOWN_AIRCRAFT:
                    controller.model().set(DroneModel.UNKNOWN);
                default :
                    controller.model().set(DroneModel.NOT_SUPPORTED);
            }
        }

        MainLogger.logger.write_message(LoggerTypes.PRODUCT_CHANGES,"Product Change Detected : " + controller.model().value() +
                MainLogger.TAB + "Last Camera           : "            + lastDjiCamera.name() +
                MainLogger.TAB + "Last Battery          : "           + lastDjiBattery.name() +
                MainLogger.TAB + "Last Flight Controller: " + lastDjiFlightController.name() +
                MainLogger.TAB + "Last Remote Controller: " + lastDjiRemoteController.name() +
                MainLogger.TAB + "Last Gimbal           : "       + lastDjiGimbal.name() +
                MainLogger.TAB + "Last AirLink          : "           + lastDjiAirLink.name() + "\n" +
                MainLogger.TAB + "New Camera            : "             + djiBaseComponentToConnectivityType(djiCamera).name() +
                MainLogger.TAB + "New Battery           : "            + djiBaseComponentToConnectivityType(djiBattery).name() +
                MainLogger.TAB + "New Flight Controller : "  + djiBaseComponentToConnectivityType(djiFlightController).name() +
                MainLogger.TAB + "New Remote Controller : "  + djiBaseComponentToConnectivityType(djiRemoteController).name() +
                MainLogger.TAB + "New Gimbal            : "        + djiBaseComponentToConnectivityType(djiGimbal).name() +
                MainLogger.TAB + "New AirLink           : "            + djiBaseComponentToConnectivityType(djiAirLink).name());

        lastDjiCamera           = djiBaseComponentToConnectivityType(djiCamera);
        lastDjiBattery          = djiBaseComponentToConnectivityType(djiBattery);
        lastDjiFlightController = djiBaseComponentToConnectivityType(djiFlightController);
        lastDjiRemoteController = djiBaseComponentToConnectivityType(djiRemoteController);
        lastDjiGimbal           = djiBaseComponentToConnectivityType(djiGimbal);
        lastDjiAirLink          = djiBaseComponentToConnectivityType(djiAirLink);

        controller.camera().getConnectivity().set(lastDjiCamera);
        controller.getDroneBattery().getConnectivity().set(lastDjiBattery);
        MainLogger.logger.write_message(LoggerTypes.PRODUCT_CHANGES,"setting connectivity for flight controller : " + lastDjiFlightController);
        MainLogger.logger.write_message(LoggerTypes.PRODUCT_CHANGES,"last connectivity for flight controller : " + controller.flightTasks().getConnectivity().value());
        controller.flightTasks().getConnectivity().set(lastDjiFlightController);
        controller.droneHome().getConnectivity().set(lastDjiFlightController);
        controller.getRemoteController().getConnectivity().set(lastDjiRemoteController);
        controller.gimbal().getConnectivity().set(lastDjiGimbal);
        controller.getAirLink().getConnectivity().set(lastDjiAirLink);
    }

    public void inShadowOfHardwareChange(){
        controller.connectivity().set(DroneConnectivity.REFRESHING);
    }

    private ComponentConnectivityType djiBaseComponentToConnectivityType(BaseComponent component){

        if(component == null){
            return ComponentConnectivityType.NULL;
        }
        else{
            if(component.isConnected()){
                return ComponentConnectivityType.CONNECTED;
            }
            else{
                return ComponentConnectivityType.NOT_CONNECTED;
            }
        }
    }
}
