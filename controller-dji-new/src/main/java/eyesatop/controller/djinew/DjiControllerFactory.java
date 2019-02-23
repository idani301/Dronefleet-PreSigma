package eyesatop.controller.djinew;

import android.content.Context;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import eyesatop.controller.ControllerFactory;
import eyesatop.controller.DroneController;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.future.Future;
import eyesatop.util.future.SettableFuture;
import eyesatop.util.geo.ObstacleProvider;


/**
 * Created by einav on 19/01/2017.
 */

public class DjiControllerFactory implements ControllerFactory {

    private final Context context;
    private boolean isInitDone = false;
    private final ObstacleProvider obstacleProvider;
    private final UUID uuid;
    private ScheduledFuture notifyHardwareChange;
    private ScheduledExecutorService updateHardwareChangeThread = Executors.newSingleThreadScheduledExecutor();


    public DjiControllerFactory(Context context, ObstacleProvider obstacleProvider) {
        this.context = context;
        this.obstacleProvider = obstacleProvider;
        uuid = null;
    }


    public DjiControllerFactory(Context context, ObstacleProvider obstacleProvider,UUID uuid) {
        this.context = context;
        this.obstacleProvider = obstacleProvider;
        this.uuid = uuid;
    }

    @Override
    public Future<DroneController> newController() throws Exception {

        final SettableFuture<DroneController> future = new SettableFuture<>();

        final ControllerDjiNew droneController = new ControllerDjiNew(obstacleProvider,uuid);

        MainLogger.logger.write_message(LoggerTypes.SDK_INIT,"Trying to register the app");

        DJISDKManager.getInstance().registerApp(context, new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError djiError) {
                if(isInitDone){
                    MainLogger.logger.write_message(LoggerTypes.SDK_INIT,"Prevented double init");
                    return;
                }
                isInitDone = true;

                if(DJISDKError.REGISTRATION_SUCCESS.equals(djiError)) {
                    MainLogger.logger.write_message(LoggerTypes.SDK_INIT,"Success Init");

                    future.set(droneController);
                    tickHardwareChange(droneController);
                }
                else{
                    MainLogger.logger.write_message(LoggerTypes.SDK_INIT,"Failed Init, Reason: " + (djiError == null ? "djiError = NULL" : djiError.getDescription()));
                    future.set(null);
                }
            }

            @Override
            public void onProductDisconnect() {
                tickHardwareChange(droneController);
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                tickHardwareChange(droneController);
            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {
                tickHardwareChange(droneController);
            }
        });

        return future;
    }

    private void tickHardwareChange(final ControllerDjiNew controller){

        MainLogger.logger.write_message(LoggerTypes.PRODUCT_CHANGES,"Tick Hardware Change");

        controller.getHardwareManager().inShadowOfHardwareChange();

        if(notifyHardwareChange != null) {
            notifyHardwareChange.cancel(false);
        }

        notifyHardwareChange = updateHardwareChangeThread.schedule(new Runnable() {
            @Override
            public void run() {
                MainLogger.logger.write_message(LoggerTypes.PRODUCT_CHANGES,"Doing hardware change");
                controller.getHardwareManager().hardwareChanged();
            }
        }, 1, TimeUnit.SECONDS);
    }
}
