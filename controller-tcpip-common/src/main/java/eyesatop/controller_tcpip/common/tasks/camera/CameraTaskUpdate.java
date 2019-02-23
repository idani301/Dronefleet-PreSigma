package eyesatop.controller_tcpip.common.tasks.camera;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetCameraMode;
import eyesatop.controller.tasks.camera.SetZoomLevel;
import eyesatop.controller.tasks.camera.StartLiveStream;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasks.TaskUpdate;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public abstract class CameraTaskUpdate extends TaskUpdate<CameraTaskType> {

    @JsonCreator
    public CameraTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                            @JsonProperty(ERROR) DroneTaskException error,
                            @JsonProperty(STATUS) TaskStatus status) {
        super(uuid, error, status);
    }

    @JsonIgnore
    public static CameraTaskUpdate getFromTask(DroneTask<CameraTaskType> cameraTask){
        switch (cameraTask.taskType()){

            case STOP_LIVE_STREAM:
                return new StopLiveStreamTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case START_LIVE_STREAM:
                StartLiveStream startLiveStream = (StartLiveStream) cameraTask;
                return new StartLiveStreamTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value(),startLiveStream.url());
            case START_RECORD:
                return new StartRecordingTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case STOP_RECORD:
                return new StopRecordingTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case STOP_SHOOTING_PHOTOS:
                return new StopShootingPhotosTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case TAKE_PHOTO:
                return new TakePhotoTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case SET_ZOOM_LEVEL:
                SetZoomLevel setZoomLevel = (SetZoomLevel) cameraTask;
                return new SetZoomLevelTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value(),setZoomLevel.zoomLevel());
            case FORMAT_SD_CARD:
                return new FormatSDCardTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case ZOOM_IN:
                return new ZoomInTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case ZOOM_OUT:
                return new ZoomOutTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value());
            case CHANGE_MODE:
                SetCameraMode setCameraMode = (SetCameraMode) cameraTask;
                return new SetCameraModeTaskUpdate(cameraTask.uuid(),cameraTask.error().value(),cameraTask.status().value(),setCameraMode.mode());
                default:
                    return null;
        }
    }
}
