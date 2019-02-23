package eyesatop.controller;

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
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;

public interface DroneCamera {

    ObservableValue<DroneTask<CameraTaskType>> currentTask();
    ObservableList<CameraTaskBlockerType> tasksBlockers();

    ObservableValue<CameraMode> mode();
    ObservableBoolean recording();
    ObservableBoolean isShootingPhoto();
    ObservableValue<Integer> recordTimeInSeconds();
    ObservableValue<Integer> shootPhotoIntervalValue();
    ObservableValue<MediaStorage> mediaStorage();
    ObservableValue<VideoPacket> videoBuffer();

    ObservableValue<ZoomInfo> zoomInfo();

    ObservableValue<Double> zoomLevel();
    ObservableBoolean isZoomSupported();

    ObservableValue<StreamState> streamState();

    StartLiveStream startLiveStream(String url) throws DroneTaskException;
    StopLiveStream stopLiveStream() throws DroneTaskException;

    ZoomIn zoomIn() throws DroneTaskException;
    ZoomOut zoomOut() throws DroneTaskException;

    FormatSDCard formatSDCard()                               throws DroneTaskException;
    SetZoomLevel setZoomLevel(double zoomLevel)               throws DroneTaskException;
    SetOpticalZoomLevel setOpticalZoomLevel(double zoomLevel) throws DroneTaskException;
    SetCameraMode setMode(CameraMode mode)                    throws DroneTaskException;
    StartRecording startRecording()                           throws DroneTaskException;
    StopRecording stopRecording()                             throws DroneTaskException;
    StopShootingPhotos stopShootingPhotos()                   throws DroneTaskException;
    TakePhoto takePhoto()                                     throws DroneTaskException;
    TakePhotoInInterval takePhotoInInterval(int captureCount,
                                            int interval) throws DroneTaskException;
}
