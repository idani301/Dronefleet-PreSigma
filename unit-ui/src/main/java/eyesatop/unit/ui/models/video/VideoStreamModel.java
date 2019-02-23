package eyesatop.unit.ui.models.video;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;

import eyesatop.util.android.VideoCodec;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.VideoPacket;
import eyesatop.unit.DroneUnit;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.util.Removable;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

/**
 * Created by einav on 25/06/2017.
 */

public class VideoStreamModel {

    private ExecutorService videoStreamExecutor = Executors.newSingleThreadExecutor();

    private final Property<VideoSurfaceInfo> videoSurfaceInfo = new Property<>();
    private final ImageViewModel noVideoIcon;

    private final DroneTabsModel tabsModel;
    private final TextureView videoSurface;

    private final DroneUnit unit;
    private final Activity activity;
    private final VideoCodec videoCodec;
    private final BooleanProperty hasVideoStream = new BooleanProperty(false);

    private Removable selectedDroneConnectivityRemovable = Removable.STUB;
    private Removable cameraListenerRemovable = Removable.STUB;

    public VideoStreamModel(Activity activity, DroneUnit unit, DroneTabsModel tabsModel, TextureView videoSurface, VideoCodec videoCodec) {
        this.tabsModel = tabsModel;
        this.videoSurface = videoSurface;
        this.activity = activity;
        this.unit = unit;
        this.videoCodec = videoCodec;

        noVideoIcon = new ImageViewModel((ImageView) activity.findViewById(R.id.ivNoVideo));
        noVideoIcon.visibility().bind(hasVideoStream.toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE));

        startListeners();
    }

    private void startListeners(){

        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                if(newValue != null && newValue.getDroneController() != null){
                    unit.setSelectedDrone(newValue.getDroneController());
                }
            }
        }).observeCurrentValue();

        videoSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                videoSurfaceInfo.set(new VideoSurfaceInfo(surface,width,height));
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                videoSurfaceInfo.set(new VideoSurfaceInfo(surface,width,height));
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                videoSurfaceInfo.set(null);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

        videoSurfaceInfo.observe(new Observer<VideoSurfaceInfo>() {
            @Override
            public void observe(VideoSurfaceInfo oldValue, VideoSurfaceInfo newValue, Observation<VideoSurfaceInfo> observation) {

                videoCodec.clean();

                if(newValue != null) {
                    videoCodec.init(activity, newValue.getSurfaceTexture(), newValue.getWidth(), newValue.getHeight());
                }
            }
        },UI_EXECUTOR);

        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                cleanVideoSurface(false);
            }
        }).observeCurrentValue();

        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {

                VideoSurfaceInfo surfaceInfo = videoSurfaceInfo.value();
                if(surfaceInfo != null && newValue != null && newValue.getDroneController() != null){
                    connectSurfaceAndDrone(surfaceInfo,newValue.getDroneController());
                }
            }
        },videoStreamExecutor);

        videoSurfaceInfo.observe(new Observer<VideoSurfaceInfo>() {
            @Override
            public void observe(VideoSurfaceInfo oldValue, VideoSurfaceInfo newValue, Observation<VideoSurfaceInfo> observation) {

                if(newValue == null){
                    return;
                }

                DroneTabModel tabModel = tabsModel.selected().value();

                if(tabModel != null && tabModel.getDroneController() != null){
                    connectSurfaceAndDrone(newValue,tabModel.getDroneController());
                }
            }
        },videoStreamExecutor).observeCurrentValue();
    }

    private synchronized void connectSurfaceAndDrone(final VideoSurfaceInfo videoSurfaceInfo, final DroneController droneController){

        selectedDroneConnectivityRemovable.remove();
        selectedDroneConnectivityRemovable = droneController.connectivity().observe(new Observer<DroneConnectivity>() {
            @Override
            public void observe(DroneConnectivity oldValue, DroneConnectivity newValue, Observation<DroneConnectivity> observation) {

                MainLogger.logger.write_message(LoggerTypes.VIDEO,"Drone Connectivity change detected : " + newValue);

                cleanVideoSurface(false);
                if(newValue == DroneConnectivity.DRONE_CONNECTED){
                    startVideoStream(videoSurfaceInfo,droneController);
                }
            }
        },videoStreamExecutor).observeCurrentValue();
    }

    private final ScheduledExecutorService noVideoExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture noVideoFuture = null;
    private final NoVideoRunnable noVideoRunnable = new NoVideoRunnable();

    private class NoVideoRunnable implements Runnable{

        @Override
        public void run() {
            hasVideoStream.set(false);
        }
    }

    private void startVideoStream(VideoSurfaceInfo videoSurfaceInfo, DroneController droneController) {

        MainLogger.logger.write_message(LoggerTypes.VIDEO,"Starting video stream");

        cameraListenerRemovable = droneController.camera().videoBuffer().observe(new Observer<VideoPacket>() {
            @Override
            public void observe(VideoPacket oldValue, VideoPacket newValue, Observation<VideoPacket> observation) {

                try {
                    videoCodec.sendDataToDecoder(newValue.getData(), newValue.getSize());
                } catch (Exception e) {
                }

                if(noVideoFuture != null){
                    noVideoFuture.cancel(false);
                }
                hasVideoStream.setIfNew(true);

                noVideoFuture = noVideoExecutor.schedule(noVideoRunnable,2, TimeUnit.SECONDS);
            }
        }
        );
    }

    private void cleanVideoSurface(final boolean isSecondTry){

        MainLogger.logger.write_message(LoggerTypes.VIDEO,"Clean video surface");

        cameraListenerRemovable.remove();
        cameraListenerRemovable = Removable.STUB;

        UI_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                videoCodec.clean();

                VideoSurfaceInfo surfaceInfo =  videoSurfaceInfo.value();
                if(surfaceInfo != null) {

                    try {

                        Surface surface = new Surface(surfaceInfo.getSurfaceTexture());
                        Canvas canvas = surface.lockCanvas(new Rect(0, 0, surfaceInfo.getWidth(), surfaceInfo.getHeight()));
                        try {
                            canvas.drawColor(ContextCompat.getColor(activity, R.color.background));
                        } finally {
                            surface.unlockCanvasAndPost(canvas);
                            surface.release();
                        }
                    }
                    catch (Exception e){
                        MainLogger.logger.writeError(LoggerTypes.ERROR,e);
                    }

                    try {
                        videoCodec.init(activity, surfaceInfo.getSurfaceTexture(), surfaceInfo.getWidth(), surfaceInfo.getHeight());
                    }
                    catch (Exception e){
                        MainLogger.logger.writeError(LoggerTypes.ERROR,e);
                        if(!isSecondTry) {
                            cleanVideoSurface(true);
                        }
                    }
                }
            }
        });
    }

    public Property<VideoSurfaceInfo> getVideoSurfaceInfo() {
        return videoSurfaceInfo;
    }
}
