package eyesatop.controller.mock;

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

public abstract class MockDroneCamera implements DroneCamera {

    public static class Stub extends MockDroneCamera {

        @Override
        public StartLiveStream startLiveStream(String url) throws DroneTaskException {
            return null;
        }

        @Override
        public StopLiveStream stopLiveStream() throws DroneTaskException {
            return null;
        }

        @Override
        public ZoomIn zoomIn() throws DroneTaskException {
            return null;
        }

        @Override
        public ZoomOut zoomOut() throws DroneTaskException {
            return null;
        }

        @Override
        public FormatSDCard formatSDCard() throws DroneTaskException {
            return null;
        }

        @Override
        public SetZoomLevel setZoomLevel(double zoomLevel) throws DroneTaskException {
            return null;
        }

        @Override
        public SetOpticalZoomLevel setOpticalZoomLevel(double zoomLevel) throws DroneTaskException {
            return null;
        }

        @Override public SetCameraMode setMode(CameraMode mode) throws DroneTaskException {return null;}
        @Override public StartRecording startRecording() throws DroneTaskException {return null;}
        @Override public StopRecording stopRecording() throws DroneTaskException {return null;}
        @Override public TakePhoto takePhoto() throws DroneTaskException {return null;}
        @Override public StopShootingPhotos stopShootingPhotos() throws DroneTaskException { return null; }

        @Override
        public TakePhotoInInterval takePhotoInInterval(int captureCount, int interval) throws DroneTaskException { return null; }
    }

    private final Property<DroneTask<CameraTaskType>> currentTask;
    private final ObservableList<CameraTaskBlockerType> tasksBlockers;
    private final Property<CameraMode> mode;
    private final BooleanProperty isShootingPhoto;
    private final Property<Double> zoomLevel;
    private final Property<Integer> recordTimeInSeconds;
    private final BooleanProperty recording;
    private final Property<MediaStorage> mediaStorage;
    private final Property<VideoPacket> videoBuffer;
    private final BooleanProperty isZoomSupported;
    private final Property<Integer> shootPhotoInIntervalValue = new Property<>();
    private final Property<ZoomInfo> zoomInfo = new Property<>();
    private final Property<StreamState> streamState = new Property<>();

    private final Property<Double> opticalZoomLevel = new Property<>();
    private final BooleanProperty isOpticalZoomSupported = new BooleanProperty();

    protected MockDroneCamera() {
        isShootingPhoto = new BooleanProperty();
        recordTimeInSeconds = new Property<>();
        currentTask = new Property<>();
        tasksBlockers = new ObservableList<>();
        mode = new Property<>();
        recording = new BooleanProperty();
        mediaStorage = new Property<>();
        videoBuffer = new Property<>();
        zoomLevel = new Property<>();
        isZoomSupported = new BooleanProperty();
    }

    @Override
    public Property<StreamState> streamState() {
        return streamState;
    }

    @Override
    public Property<ZoomInfo> zoomInfo() {
        return zoomInfo;
    }

    @Override
    public Property<Double> zoomLevel() {
        return zoomLevel;
    }

    @Override
    public BooleanProperty isZoomSupported() {
        return isZoomSupported;
    }

    @Override
    public BooleanProperty isShootingPhoto() {
        return isShootingPhoto;
    }

    @Override
    public Property<Integer> shootPhotoIntervalValue() {
        return shootPhotoInIntervalValue;
    }

    @Override public Property<Integer> recordTimeInSeconds() {return recordTimeInSeconds;}
    @Override public Property<DroneTask<CameraTaskType>> currentTask() {return currentTask;}
    @Override public ObservableList<CameraTaskBlockerType> tasksBlockers() {return tasksBlockers;}
    @Override public Property<CameraMode> mode() {return mode;}
    @Override public BooleanProperty recording() {return recording;}
    @Override public Property<MediaStorage> mediaStorage() {return mediaStorage;}
    @Override public Property<VideoPacket> videoBuffer() {return videoBuffer;}
}
