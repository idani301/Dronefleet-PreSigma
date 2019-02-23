package eyesatop.controller.djinew.components;

import com.example.abstractcontroller.components.AbstractDroneAirLink;

import dji.sdk.airlink.AirLink;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.airlink.AirLinkTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 12/09/2017.
 */

enum AirLinkType {
    LB,
    OCU_SYNC,
    WIFI,
    UNKNOWN;
}
public class DroneAirLinkDji extends AbstractDroneAirLink {


    private boolean everStartedLB = false;
    private boolean everStartedOcuSync = false;
    private AirLinkType airLinkType = AirLinkType.UNKNOWN;
    private BaseComponent currentComponent = null;

    private final ControllerDjiNew controller;

    public DroneAirLinkDji(ControllerDjiNew controller) {
        this.controller = controller;
//
//        controller.connectivity().observe(new Observer<DroneConnectivity>() {
//            @Override
//            public void observe(DroneConnectivity oldValue, DroneConnectivity newValue, Observation<DroneConnectivity> observation) {
//
//                if(newValue != null && newValue == DroneConnectivity.DRONE_CONNECTED){
//                    AirLink currentAirLink = getDjiAirLink();
//                    if(currentAirLink != null && currentAirLink.isLightbridgeLinkSupported()){
//                        LightbridgeLink lightbridgeLink = currentAirLink.getLightbridgeLink();
//                        lightbridgeLink.setChannelSelectionMode(ChannelSelectionMode.AUTO, new CommonCallbacks.CompletionCallback() {
//                            @Override
//                            public void onResult(DJIError djiError) {
//                                MainLogger.logger.write_message(LoggerTypes.AIRLINK,"Done Changing selection mode, result : " + (djiError == null ? "SUCCESS" : djiError.getDescription()));
//                            }
//                        });
//
//                        lightbridgeLink.setDataRate(LightbridgeDataRate.BANDWIDTH_4_MBPS, new CommonCallbacks.CompletionCallback() {
//                            @Override
//                            public void onResult(DJIError djiError) {
//                                MainLogger.logger.write_message(LoggerTypes.AIRLINK,"Done Changing data rate, result : " + (djiError == null ? "SUCCESS" : djiError.getDescription()));
//                            }
//                        });
//
//                        lightbridgeLink.setTransmissionMode(LightbridgeTransmissionMode.HIGH_QUALITY, new CommonCallbacks.CompletionCallback() {
//                            @Override
//                            public void onResult(DJIError djiError) {
//                                MainLogger.logger.write_message(LoggerTypes.AIRLINK,"Done setting transmittion mode to high quality, result : " + (djiError == null ? "SUCCESS" : djiError.getDescription()));
//                            }
//                        });
//
//                        lightbridgeLink.setBandwidthAllocationForHDMIVideoInputPort(0, new CommonCallbacks.CompletionCallback() {
//                            @Override
//                            public void onResult(DJIError djiError) {
//                                MainLogger.logger.write_message(LoggerTypes.AIRLINK,"Done setBandwidthAllocationForHDMIVideoInputPort, result : " + (djiError == null ? "SUCCESS" : djiError.getDescription()));
//                            }
//                        });
//                    }
//                }
//            }
//        });

    }

    @Override
    protected RunnableDroneTask<AirLinkTaskType> stubToRunnable(StubDroneTask<AirLinkTaskType> stubDroneTask) throws DroneTaskException {
        return null;
    }

    @Override
    public void onComponentAvailable() {

        if(getDjiAirLink() == null){
            airLinkType = AirLinkType.UNKNOWN;
            currentComponent = null;
        }
        else if(getDjiAirLink().isLightbridgeLinkSupported()){
            airLinkType = AirLinkType.LB;
            currentComponent = getDjiAirLink().getLightbridgeLink();
        }
        else if(getDjiAirLink().isOcuSyncLinkSupported()){
            airLinkType = AirLinkType.OCU_SYNC;
            currentComponent = getDjiAirLink().getOcuSyncLink();
        }
        else if(getDjiAirLink().isWiFiLinkSupported()){
            airLinkType = AirLinkType.WIFI;
            currentComponent = getDjiAirLink().getWiFiLink();
        }
        else{
            airLinkType = AirLinkType.UNKNOWN;
            currentComponent = null;
        }

        switch (airLinkType){

            case LB:

                if (everStartedLB) {
                    return;
                }

//                ((LightbridgeLink)currentComponent).setChannelInterferenceCallback(new LightbridgeLink.ChannelInterferenceCallback() {
//                    @Override
//                    public void onResult(ChannelInterference[] channelInterferences) {
//
//                        String signalString = "Got new data: ";
//
//                        if(channelInterferences != null && channelInterferences.length > 0){
//
//                            int sum = 0;
//                            for(ChannelInterference interference : channelInterferences){
//
//                                signalString += MainLogger.TAB  + interference.getChannel() + "," + interference.getPower();
//                                sum += interference.getPower();
//                            }
//                            sum = Math.abs(sum);
//                            float average = (float)sum/(float)channelInterferences.length;
//                            int currentPercent = (int)((average-60)*2.5);
//                            rcSignalStrengthPercent().setIfNew(currentPercent);
//                        }
//                        else{
//                            signalString += " NULL or size 0";
//                            rcSignalStrengthPercent().setIfNew(null);
//                        }
//                        MainLogger.logger.write_message(LoggerTypes.AIRLINK,signalString);
//                    }
//                });
                break;
            case OCU_SYNC:

                if(everStartedOcuSync){
                    return;
                }

//                ((OcuSyncLink)currentComponent).setChannelInterferenceCallback(new OcuSyncLink.ChannelInterferenceCallback() {
//                    @Override
//                    public void onUpdate(FrequencyInterference[] frequencyInterferences) {
//
//                        int sum = 0;
//                        for(FrequencyInterference interference : frequencyInterferences){
//                            MainLogger.logger.write_message(LoggerTypes.AIRLINK,"Ocu Link signal Interface = " +
//                                    MainLogger.TAB + "Rssi     : " + interference.rssi +
//                                    MainLogger.TAB + "Freq From: " + interference.frequencyFrom +
//                                    MainLogger.TAB + "Freq To  : " + interference.frequencyTo);
//                            sum += interference.rssi;
//                        }
//                        float average = (float)sum/(float)frequencyInterferences.length;
//                        average = Math.abs(average);
//                        int currentPercent = (int)(average/1.08F);
//
//
//                        MainLogger.logger.write_message(LoggerTypes.AIRLINK,"Ocu Link signal strength = " + currentPercent);
//
//                        rcSignalStrengthPercent().setIfNew(currentPercent);
//                    }
//                });
                break;
            case WIFI:
                break;
            case UNKNOWN:
                break;
        }
    }

    private AirLink getDjiAirLink(){

        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if(product != null && product instanceof Aircraft){
            Aircraft aircraft = (Aircraft) product;
            return aircraft.getAirLink();
        }
        return null;
    }


    @Override
    public void onComponentConnected() {

    }
}
