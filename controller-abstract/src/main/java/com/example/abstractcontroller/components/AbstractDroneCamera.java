package com.example.abstractcontroller.components;

import com.example.abstractcontroller.taskmanager.CameraTaskManager;

import eyesatop.controller.DroneCamera;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.beans.MediaStorage;
import eyesatop.controller.beans.StreamState;
import eyesatop.controller.beans.VideoPacket;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.FormatSDCard;
import eyesatop.controller.tasks.camera.SetCameraMode;
import eyesatop.controller.tasks.camera.SetOpticalZoomLevel;
import eyesatop.controller.tasks.camera.SetZoomLevel;
import eyesatop.controller.tasks.camera.StartLiveStream;
import eyesatop.controller.tasks.camera.StartRecording;
import eyesatop.controller.tasks.camera.StopLiveStream;
import eyesatop.controller.tasks.camera.StopRecording;
import eyesatop.controller.tasks.camera.StopShootingPhotos;
import eyesatop.controller.tasks.camera.TakePhoto;
import eyesatop.controller.tasks.camera.TakePhotoInInterval;
import eyesatop.controller.tasks.camera.ZoomIn;
import eyesatop.controller.tasks.camera.ZoomOut;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 28/08/2017.
 */

public abstract class AbstractDroneCamera extends GeneralDroneComponent<CameraTaskType,CameraTaskBlockerType> implements DroneCamera {

    private final Property<CameraMode> mode;
    private final BooleanProperty isShootingPhoto;
    private final Property<Integer> recordTimeInSeconds;
    private final BooleanProperty recording;
    private final Property<MediaStorage> mediaStorage;
    private final Property<VideoPacket> videoBuffer;

    private final Property<StreamState> streamState = new Property<>();

    private final Property<ZoomInfo> zoomInfo = new Property<>();

    private final Property<Double> zoomLevel;
    private final BooleanProperty isZoomSupported;

    private final Property<Integer> shootPhotoIntervalValue;

    protected AbstractDroneCamera(){
        super(new CameraTaskManager());

        isShootingPhoto = new BooleanProperty();
        recordTimeInSeconds = new Property<>();
        mode = new Property<>();
        recording = new BooleanProperty();
        mediaStorage = new Property<>();
        videoBuffer = new Property<>();
        zoomLevel = new Property<>();
        isZoomSupported = new BooleanProperty();
        shootPhotoIntervalValue = new Property<>();
    }

    @Override
    public Property<StreamState> streamState() {
        return streamState;
    }

    @Override
    public ObservableValue<DroneTask<CameraTaskType>> currentTask() {
        return taskManager.currentTask();
    }

    @Override
    public ObservableList<CameraTaskBlockerType> tasksBlockers() {
        return taskManager.getTasksBlockers();
    }

    @Override
    public BooleanProperty isShootingPhoto() {
        return isShootingPhoto;
    }

    @Override
    public Property<CameraMode> mode() {
        return mode;
    }

    @Override
    public BooleanProperty recording() {
        return recording;
    }

    @Override
    public Property<Integer> recordTimeInSeconds() {
        return recordTimeInSeconds;
    }

    @Override
    public Property<MediaStorage> mediaStorage() {
        return mediaStorage;
    }

    @Override
    public Property<VideoPacket> videoBuffer() {
        return videoBuffer;
    }

    @Override
    public Property<Double> zoomLevel() {
        return zoomLevel;
    }

    @Override
    public SetCameraMode setMode(final CameraMode mode) throws DroneTaskException {

        SetCameraMode.StubSetCameraMode stubTask = new SetCameraMode.StubSetCameraMode(mode) {
            @Override
            public CameraMode mode() {
                return mode;
            }
        };

        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public SetOpticalZoomLevel setOpticalZoomLevel(double zoomLevel) throws DroneTaskException {
        SetOpticalZoomLevel.SetOpticalZoomLevelStub stubTask = new SetOpticalZoomLevel.SetOpticalZoomLevelStub(zoomLevel);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public SetZoomLevel setZoomLevel(final double zoomLevel) throws DroneTaskException {
        SetZoomLevel.SetZoomLevelStub stubTask = new SetZoomLevel.SetZoomLevelStub(){

            @Override
            public double zoomLevel() {
                return zoomLevel;
            }
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public StartRecording startRecording() throws DroneTaskException {

        StartRecording.StubStartRecording stubTask = new StartRecording.StubStartRecording() {
        };

        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public StopRecording stopRecording() throws DroneTaskException {

        StopRecording.StubStopRecording stubTask = new StopRecording.StubStopRecording() {
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public StopShootingPhotos stopShootingPhotos() throws DroneTaskException {

        StopShootingPhotos.StubStopShootingPhotos stubTask = new StopShootingPhotos.StubStopShootingPhotos();
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public TakePhoto takePhoto() throws DroneTaskException {
        TakePhoto.StubTakePhoto stubTask = new TakePhoto.StubTakePhoto();
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public TakePhotoInInterval takePhotoInInterval(int captureCount,int interval) throws DroneTaskException {

        TakePhotoInInterval.StubTakePhotoInInterval stubTask = new TakePhotoInInterval.StubTakePhotoInInterval(captureCount, interval);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public StartLiveStream startLiveStream(String url) throws DroneTaskException {

        StartLiveStream.StartLiveStreamStub stubTask = new StartLiveStream.StartLiveStreamStub(url);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public StopLiveStream stopLiveStream() throws DroneTaskException {
        StopLiveStream.StopLiveStreamStub stubTask = new StopLiveStream.StopLiveStreamStub();
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public FormatSDCard formatSDCard() throws DroneTaskException {

        FormatSDCard.FormatSDCardStub stubTask = new FormatSDCard.FormatSDCardStub();
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public Property<ZoomInfo> zoomInfo() {
        return zoomInfo;
    }

    @Override
    public ZoomIn zoomIn() throws DroneTaskException {
        ZoomIn.ZoomInStub stubTask = new ZoomIn.ZoomInStub();
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public ZoomOut zoomOut() throws DroneTaskException {
        ZoomOut.ZoomOutStub stubTask = new ZoomOut.ZoomOutStub();
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public BooleanProperty isZoomSupported() {
        return isZoomSupported;
    }

    @Override
    public Property<Integer> shootPhotoIntervalValue() {
        return shootPhotoIntervalValue;
    }

    @Override
    public void clearData() {

        isShootingPhoto.set(null);
        mode.set(null);
        recordTimeInSeconds.set(null);
        recording.set(null);
        mediaStorage.set(null);
        videoBuffer.set(null);
        zoomLevel.set(null);
        isZoomSupported.set(null);
        shootPhotoIntervalValue.set(null);
        streamState.set(null);
    }

    public abstract void internalFormatSDCard() throws DroneTaskException;
}
