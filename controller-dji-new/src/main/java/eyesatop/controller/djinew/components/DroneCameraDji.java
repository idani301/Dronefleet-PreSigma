package eyesatop.controller.djinew.components;

import android.os.Environment;
import android.support.annotation.NonNull;
//import android.support.annotation.NonNull;

import com.example.abstractcontroller.components.AbstractDroneCamera;
import com.example.abstractcontroller.tasks.camera.FormatSDCardAbstract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.StorageState;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.beans.MediaStorage;
import eyesatop.controller.beans.StreamState;
import eyesatop.controller.beans.VideoCodecType;
import eyesatop.controller.beans.VideoPacket;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.djinew.tasks.camera.DjiSetCameraMode;
import eyesatop.controller.djinew.tasks.camera.DjiSetZoomLevel;
import eyesatop.controller.djinew.tasks.camera.DjiStartLiveStream;
import eyesatop.controller.djinew.tasks.camera.DjiStartRecording;
import eyesatop.controller.djinew.tasks.camera.DjiStopLiveStream;
import eyesatop.controller.djinew.tasks.camera.DjiStopRecording;
import eyesatop.controller.djinew.tasks.camera.DjiStopShootingPhotos;
import eyesatop.controller.djinew.tasks.camera.DjiTakePhoto;
import eyesatop.controller.djinew.tasks.camera.DjiTakePhotoInInterval;
import eyesatop.controller.djinew.tasks.camera.DjiZoomIn;
import eyesatop.controller.djinew.tasks.camera.DjiZoomOut;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetCameraMode;
import eyesatop.controller.tasks.camera.SetZoomLevel;
import eyesatop.controller.tasks.camera.StartLiveStream;
import eyesatop.controller.tasks.camera.StopLiveStream;
import eyesatop.controller.tasks.camera.TakePhotoInInterval;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.android.files.UniqueFile;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 09/09/2017.
 */

public class DroneCameraDji extends AbstractDroneCamera {

    private static final String VIDEO_DIRECTORY = "EyesatopVideos";
    private final ControllerDjiNew controller;
    private boolean isCameraCallbacksInit = false;

    private final Property<Long> lastInjectedTime = new Property<>();
    private final BooleanProperty recordVideo = new BooleanProperty(false);

    private final Property<SettingsDefinitions.ShootPhotoMode> currentShootPhotoMode = new Property<>();

    private final ExecutorService streamCheckExecutor = Executors.newSingleThreadExecutor();

    final List<VideoPacket> packetsRecordList = new ArrayList<>();

    public DroneCameraDji(ControllerDjiNew controller) {

        this.controller = controller;
    }

    private final Property<SettingsDefinitions.OpticalZoomSpec> opticalSpec = new Property<>();
    private final Property<Integer> currentFocalLength = new Property<>();

    private final Property<Integer> maxDigitalFactor = new Property<>();

    private final ExecutorService refreshZoomInfoExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected RunnableDroneTask<CameraTaskType> stubToRunnable(StubDroneTask<CameraTaskType> stubDroneTask) throws DroneTaskException {

        switch (stubDroneTask.taskType()){

            case START_LIVE_STREAM:
                StartLiveStream startLiveStream = (StartLiveStream) stubDroneTask;
                return new DjiStartLiveStream(controller,startLiveStream.url());
            case STOP_LIVE_STREAM:
                return new DjiStopLiveStream();
            case TAKE_PHOTO_INTERVAL:
                TakePhotoInInterval takePhotoInInterval = (TakePhotoInInterval)stubDroneTask;
                return new DjiTakePhotoInInterval(controller, takePhotoInInterval.captureCount(), takePhotoInInterval.interval());
            case START_RECORD:
                return new DjiStartRecording(controller);
            case STOP_RECORD:
                return new DjiStopRecording(controller);
            case STOP_SHOOTING_PHOTOS:
                return new DjiStopShootingPhotos(controller);
            case TAKE_PHOTO:
                return new DjiTakePhoto(controller);
            case SET_ZOOM_LEVEL:
                SetZoomLevel zoomLevel = (SetZoomLevel)stubDroneTask;
                return new DjiSetZoomLevel(controller,zoomLevel.zoomLevel());
            case FORMAT_SD_CARD:
                return new FormatSDCardAbstract(controller);
            case CHANGE_MODE:
                SetCameraMode setCameraMode = (SetCameraMode)stubDroneTask;
                return new DjiSetCameraMode(controller,setCameraMode.mode());
            case ZOOM_OUT:
                return new DjiZoomOut(controller);
            case ZOOM_IN:
                return new DjiZoomIn(controller);
            default:
                throw new DroneTaskException("Not implemented : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {

        if(isCameraCallbacksInit){
            MainLogger.logger.write_message(LoggerTypes.CALLBACKS,"Camera Callback Manager, aborting restarting callbacks since already started");
            return;
        }

        isCameraCallbacksInit = true;

        refreshZoomInfoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(500);
                        refreshZoomInfo();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final VideoFeeder.VideoDataListener nonRecordCallback = new VideoFeeder.VideoDataListener() {
            @Override
            public void onReceive(byte[] bytes, int i) {
                byte[] subArray = new byte[i];
                System.arraycopy(bytes,0,subArray,0,i);

                videoBuffer().set(new VideoPacket(VideoCodecType.H264,subArray, i));
            }
        };

        VideoFeeder.VideoDataListener recordCallback = new VideoFeeder.VideoDataListener() {
            @Override
            public void onReceive(byte[] bytes, int i) {

                Long currnetTime = System.currentTimeMillis();
                Long lastInjectedTimeLong = lastInjectedTime.value();
                long interval = lastInjectedTimeLong == null ? 0 : currnetTime - lastInjectedTimeLong;
                lastInjectedTime.set(currnetTime);
                byte[] newArray = new byte[i];
                System.arraycopy(bytes, 0, newArray, 0, i);
                VideoPacket newPacket = new VideoPacket(VideoCodecType.H264, newArray, interval, i);
                packetsRecordList.add(newPacket);

                videoBuffer().set(new VideoPacket(VideoCodecType.H264, bytes, i));
            }
        };

        final Camera djiCamera = controller.getHardwareManager().getDjiCamera();

        if(djiCamera == null){
            isCameraCallbacksInit = false;
            return;
        }

//        ResolutionAndFrameRate resolutionAndFrameRate = new ResolutionAndFrameRate(SettingsDefinitions.VideoResolution.RESOLUTION_1280x720, SettingsDefinitions.VideoFrameRate.FRAME_RATE_30_FPS);
//        djiCamera.setVideoResolutionAndFrameRate(resolutionAndFrameRate, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//                MainLogger.logger.write_message(LoggerTypes.ERROR,"Done resolution and frame Rate result :  " + (djiError == null ? "SUCCESS" : djiError.getDescription()));
//            }
//        });

        djiCamera.setSystemStateCallback(new SystemState.Callback() {
            @Override
            public void onUpdate(SystemState systemState) {

                Integer recordingTime = systemState.isRecording() ? systemState.getCurrentVideoRecordingTimeInSeconds() : null;
                recordTimeInSeconds().setIfNew(recordingTime);

                recording().setIfNew(systemState.isRecording());
                mode().setIfNew(cameraModeFromDji(systemState.getMode()));

                boolean isShootingPhoto =
                                systemState.isShootingIntervalPhoto() ||
                                systemState.isShootingSinglePhoto() ||
//                                systemState.isShootingSinglePhotoInRAWFormat() ||
                                systemState.isShootingPanoramaPhoto() ||
                                systemState.isShootingBurstPhoto() ||
                                systemState.isShootingRAWBurstPhoto() ||
                                systemState.isShootingShallowFocusPhoto();

//                MainLoggerJava.logger.write_message(LoggerTypes.PHANTOM_3,"isShooting photo : " + isShootingPhoto
//                + MainLoggerJava.TAB + "systemState.isShootingIntervalPhoto() : "  + systemState.isShootingIntervalPhoto()
//                                + MainLoggerJava.TAB + "systemState.isShootingSinglePhoto() : "  + systemState.isShootingSinglePhoto()
//                                + MainLoggerJava.TAB + "systemState.isShootingSinglePhotoInRAWFormat() : "  + systemState.isShootingSinglePhotoInRAWFormat()
//                                + MainLoggerJava.TAB + "systemState.isShootingPanoramaPhoto() : "  + systemState.isShootingPanoramaPhoto()
//                                + MainLoggerJava.TAB + "systemState.isShootingBurstPhoto() : "  + systemState.isShootingBurstPhoto()
//                                + MainLoggerJava.TAB + "systemState.isShootingRAWBurstPhoto() : "  + systemState.isShootingRAWBurstPhoto()
//                                + MainLoggerJava.TAB + "systemState.isShootingShallowFocusPhoto() : "  + systemState.isShootingShallowFocusPhoto());

                isShootingPhoto().setIfNew(isShootingPhoto);
            }
        });

        djiCamera.setStorageStateCallBack(new StorageState.Callback() {
            @Override
            public void onUpdate(@NonNull StorageState storageState) {
                MediaStorage newMediaStorage = new MediaStorage(
                        storageState.getAvailableRecordingTimeInSeconds(),
                        Math.abs(storageState.getRemainingSpaceInMB()),
                        Math.abs(storageState.getTotalSpaceInMB()));

                mediaStorage().setIfNew(newMediaStorage);
            }
        });

        final LiveStreamManager liveStreamManager = DJISDKManager.getInstance().getLiveStreamManager();
        liveStreamManager.setVideoEncodingEnabled(true);
        liveStreamManager.setAudioMuted(true);

        liveStreamManager.registerListener(new LiveStreamManager.OnLiveChangeListener() {
            @Override
            public void onStatusChanged(int i) {
                StreamState newState = new StreamState(liveStreamManager.isStreaming(),liveStreamManager.getLiveUrl(),liveStreamManager.getLiveVideoBitRate());
                streamState().set(newState);
            }
        });
        ExecutorService liveStreamExeuctor = Executors.newSingleThreadExecutor();
        liveStreamExeuctor.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    StreamState newState = new StreamState(liveStreamManager.isStreaming(), liveStreamManager.getLiveUrl(), liveStreamManager.getLiveVideoBitRate());
                    streamState().set(newState);
                }
            }
        });


//        VideoFeeder.getInstance().addPhysicalSourceListener(new VideoFeeder.PhysicalSourceListener() {
//            @Override
//            public void onChange(VideoFeeder.VideoFeed videoFeed, VideoFeeder.PhysicalSource physicalSource) {
//
//                if(!videoFeed.getListeners().contains(nonRecordCallback)){
//                    videoFeed.addVideoDataListener(nonRecordCallback);
//                }
//            }
//        });

//        recordVideo.observe(new Observer<Boolean>() {
//            @Override
//            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
//
//                if(!newValue){
//                    VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(nonRecordCallback);
//                    VideoFeeder.getInstance().getSecondaryVideoFeed().setCallback(nonRecordCallback);
//                    VideoFeeder.getInstance().addPhysicalSourceListener(new VideoFeeder.PhysicalSourceListener() {
//                        @Override
//                        public void onChange(VideoFeeder.VideoFeed videoFeed, VideoFeeder.PhysicalSource physicalSource) {
//                            videoFeed.setCallback(nonRecordCallback);
//                        }
//                    });
//                }
//                else {
//                    packetsRecordList.clear();
//                    VideoFeeder.getInstance().addPhysicalSourceListener(new VideoFeeder.PhysicalSourceListener() {
//                        @Override
//                        public void onChange(VideoFeeder.VideoFeed videoFeed, VideoFeeder.PhysicalSource physicalSource) {
//                            videoFeed.setCallback(recordCallback);
//                        }
//                    });
//                    VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(recordCallback);
//                    VideoFeeder.getInstance().getSecondaryVideoFeed().setCallback(recordCallback);
//                }
//
//                if(oldValue != null && oldValue && !newValue){
//                    saveVideoRecord();
//                }
//            }
//        }).observeCurrentValue();
    }

    private void saveVideoRecord(){

        try {

            File rootFolder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/" + VIDEO_DIRECTORY);
            UniqueFile uniqueFile = new UniqueFile("Dji_Video",".txt", rootFolder);
            uniqueFile.setIncludeDate(true);
            File createdFile = uniqueFile.createUniqueFile();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(createdFile));
            objectOutputStream.writeObject(packetsRecordList);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        packetsRecordList.clear();

    }

    private CameraMode cameraModeFromDji(dji.common.camera.SettingsDefinitions.CameraMode cameraMode){

        switch(cameraMode){

            case SHOOT_PHOTO:
                return CameraMode.STILLS;
            case RECORD_VIDEO:
                return CameraMode.VIDEO;
        }

        return CameraMode.UNKNOWN;
    }

    @Override
    public void onComponentConnected() {

        refreshZoomInfo();
        refreshZoomLevelData();
        refreshOpticalZoomData();
        refreshShootPhotoMode();

        Camera djiCamera = controller.getHardwareManager().getDjiCamera();

        if(djiCamera.isDigitalZoomSupported()){
            maxDigitalFactor.set(2);
        }
        else{
            maxDigitalFactor.set(1);
        }

        djiCamera.getOpticalZoomSpec(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.OpticalZoomSpec>() {
            @Override
            public void onSuccess(SettingsDefinitions.OpticalZoomSpec opticalZoomSpec) {
                opticalSpec.set(opticalZoomSpec);
            }

            @Override
            public void onFailure(DJIError djiError) {
                opticalSpec.set(null);
            }
        });
        djiCamera.setFocusMode(SettingsDefinitions.FocusMode.AFC,null);
        djiCamera.setExposureMode(SettingsDefinitions.ExposureMode.PROGRAM,null);
    }

    public void refreshShootPhotoMode(){
        try{
            Camera djiCamera = controller.getHardwareManager().getDjiCamera();
            djiCamera.getShootPhotoMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ShootPhotoMode>() {
                @Override
                public void onSuccess(SettingsDefinitions.ShootPhotoMode shootPhotoMode) {
                    currentShootPhotoMode.set(shootPhotoMode);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    currentShootPhotoMode.set(null);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void refreshZoomLevelData(){
        try {
            Camera djiCamera = controller.getHardwareManager().getDjiCamera();
            djiCamera.getDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<Float>() {
                @Override
                public void onSuccess(Float aFloat) {
                    zoomLevel().set((double) aFloat);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    zoomLevel().set(null);
                }
            });
            isZoomSupported().set(djiCamera.isDigitalZoomSupported());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void refreshZoomInfo(){
        try{
            final Camera djiCamera = controller.getHardwareManager().getDjiCamera();

            if(djiCamera == null){
                return;
            }

            djiCamera.getOpticalZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    currentFocalLength.set(integer);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    currentFocalLength.set(null);
                }
            });

            djiCamera.getOpticalZoomFactor(new CommonCallbacks.CompletionCallbackWith<Float>() {
                @Override
                public void onSuccess(Float aFloat) {
                    final double opticalZoomFactor = aFloat.doubleValue();

                    djiCamera.getDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            zoomInfo().set(new ZoomInfo(opticalZoomFactor,aFloat.doubleValue()));
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                            zoomInfo().set(new ZoomInfo(opticalZoomFactor,1));
                        }
                    });
                }

                @Override
                public void onFailure(DJIError djiError) {
                    djiCamera.getDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<Float>() {
                        @Override
                        public void onSuccess(Float aFloat) {
                            zoomInfo().set(new ZoomInfo(1,aFloat.doubleValue()));
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                            zoomInfo().set(new ZoomInfo(1,1));
                        }
                    });
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void refreshOpticalZoomData() {
        try {
            Camera djiCamera = controller.getHardwareManager().getDjiCamera();

            djiCamera.getOpticalZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {

                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public ObservableValue<SettingsDefinitions.ShootPhotoMode> getCurrentShootPhotoMode() {
        return currentShootPhotoMode;
    }

    @Override
    public void clearData() {
        super.clearData();

        maxDigitalFactor.set(null);
        currentFocalLength.set(null);
        opticalSpec.set(null);
    }

    public BooleanProperty getInternalRecordVideo() {
        return recordVideo;
    }

    @Override
    public void internalFormatSDCard() throws DroneTaskException {
        Camera djiCamera = controller.getHardwareManager().getDjiCamera();
        if(djiCamera != null){

            final Property<DJIError> taskResult = new Property<>();
            final CountDownLatch taskLatch = new CountDownLatch(1);

            try{
                djiCamera.formatSDCard(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        taskResult.set(djiError);
                        taskLatch.countDown();
                    }
                });
                taskLatch.await();

                if(taskResult.value() != null){
                    throw new DroneTaskException(taskResult.value().getDescription());
                }
            }
            catch (DroneTaskException e1){
                throw new DroneTaskException("Internal Dji Error : " + e1.getErrorString());
            }
            catch (Exception e){
                throw new DroneTaskException("Internal Format SD Card Error : " + e.getMessage());
            }
        }
    }

    public Property<SettingsDefinitions.OpticalZoomSpec> getOpticalSpec() {
        return opticalSpec;
    }

    public Property<Integer> getCurrentFocalLength() {
        return currentFocalLength;
    }

    public Property<Integer> getMaxDigitalFactor() {
        return maxDigitalFactor;
    }
}
