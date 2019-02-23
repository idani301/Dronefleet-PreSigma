package eyesatop.controller_tcpip.remote.tcpipcontroller;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.mock.MockDroneCamera;
import eyesatop.controller.tasks.TaskStatus;
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
import eyesatop.controller_tcpip.common.tasks.camera.FormatSDCardTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.SetCameraModeTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.SetZoomLevelTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.StartLiveStreamTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.StartRecordingTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.StopLiveStreamTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.StopRecordingTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.StopShootingPhotosTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.TakePhotoTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.ZoomInTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.ZoomOutTaskUpdate;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.camera.CameraTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.FormatSDCardRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.SetCameraModeRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.SetZoomLevelRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.StartLiveStreamRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.StartRecordingRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.StopLiveStreamRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.StopRecordingRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.StopShootingPhotoRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.TakePhotoRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.ZoomInRequest;
import eyesatop.controller_tcpip.common.tasksrequests.camera.ZoomOutRequest;
import eyesatop.controller_tcpip.remote.tasks.taskmanager.TaskManager;
import eyesatop.util.connections.TimeoutInfo;
import eyesatop.util.connections.tcp.requestresponse.RequestResponseException;
import eyesatop.util.model.Property;

public class TCPCamera extends MockDroneCamera {

    private final TCPController controller;

    private final TaskManager<CameraTaskType> tasksManager;

    public TCPCamera(TCPController controller) {

        this.controller = controller;
        tasksManager = new TaskManager<>(controller);
    }

    private synchronized UUID startTask(final CameraTaskRequest taskRequest) throws DroneTaskException {

        try {
            TaskResponse response = controller.getCameraTaskRequestsClient().sendMessage(taskRequest,3,TimeUnit.SECONDS);

            if(response == null){
                throw new DroneTaskException("Had no Response");
            }

            if(response.getTaskUUID() == null){
                throw new DroneTaskException(response.getError() == null ? "N/A" : response.getError());
            }

            return response.getTaskUUID();
        } catch (InterruptedException e) {
            throw new DroneTaskException("Interrupt");
        } catch (RequestResponseException e) {
            throw new DroneTaskException(e.getErrorMessage());
        }
    }

    public TaskManager<CameraTaskType> getTasksManager() {
        return tasksManager;
    }

    @Override
    public StartLiveStream startLiveStream(String url) throws DroneTaskException {
        UUID uuid = startTask(new StartLiveStreamRequest(url));
        StartLiveStreamTaskUpdate taskUpdate = new StartLiveStreamTaskUpdate(uuid,null,TaskStatus.NOT_STARTED,url);
        return (StartLiveStream) tasksManager.getTask(taskUpdate);
    }

    @Override
    public StopLiveStream stopLiveStream() throws DroneTaskException {
        UUID uuid = startTask(new StopLiveStreamRequest());
        StopLiveStreamTaskUpdate taskUpdate = new StopLiveStreamTaskUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (StopLiveStream) tasksManager.getTask(taskUpdate);
    }

    @Override
    public ZoomIn zoomIn() throws DroneTaskException {
        UUID uuid = startTask(new ZoomInRequest());
        ZoomInTaskUpdate taskUpdate = new ZoomInTaskUpdate(uuid,null, TaskStatus.NOT_STARTED);

        return (ZoomIn) tasksManager.getTask(taskUpdate);
    }

    @Override
    public ZoomOut zoomOut() throws DroneTaskException {
        UUID uuid = startTask(new ZoomOutRequest());
        ZoomOutTaskUpdate taskUpdate = new ZoomOutTaskUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (ZoomOut) tasksManager.getTask(taskUpdate);
    }

    @Override
    public FormatSDCard formatSDCard() throws DroneTaskException {
        UUID uuid = startTask(new FormatSDCardRequest());
        FormatSDCardTaskUpdate taskUpdate = new FormatSDCardTaskUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (FormatSDCard) tasksManager.getTask(taskUpdate);
    }

    @Override
    public SetZoomLevel setZoomLevel(double zoomLevel) throws DroneTaskException {
        UUID uuid = startTask(new SetZoomLevelRequest(zoomLevel));
        SetZoomLevelTaskUpdate taskUpdate = new SetZoomLevelTaskUpdate(uuid,null,TaskStatus.NOT_STARTED,zoomLevel);
        return (SetZoomLevel) tasksManager.getTask(taskUpdate);
    }

    @Override
    public SetOpticalZoomLevel setOpticalZoomLevel(double zoomLevel) throws DroneTaskException {
        return null;
    }

    @Override
    public SetCameraMode setMode(CameraMode mode) throws DroneTaskException {
        UUID uuid = startTask(new SetCameraModeRequest(mode));
        SetCameraModeTaskUpdate taskUpdate = new SetCameraModeTaskUpdate(uuid,null,TaskStatus.NOT_STARTED,mode);

        return (SetCameraMode) tasksManager.getTask(taskUpdate);
    }

    @Override
    public StartRecording startRecording() throws DroneTaskException {
        UUID uuid = startTask(new StartRecordingRequest());
        StartRecordingTaskUpdate taskUpdate = new StartRecordingTaskUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (StartRecording) tasksManager.getTask(taskUpdate);
    }

    @Override
    public StopRecording stopRecording() throws DroneTaskException {
        UUID uuid = startTask(new StopRecordingRequest());
        StopRecordingTaskUpdate taskUpdate = new StopRecordingTaskUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (StopRecording) tasksManager.getTask(taskUpdate);
    }

    @Override
    public StopShootingPhotos stopShootingPhotos() throws DroneTaskException {
        UUID uuid = startTask(new StopShootingPhotoRequest());
        StopShootingPhotosTaskUpdate taskUpdate = new StopShootingPhotosTaskUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (StopShootingPhotos) tasksManager.getTask(taskUpdate);
    }

    @Override
    public TakePhoto takePhoto() throws DroneTaskException {
        UUID uuid = startTask(new TakePhotoRequest());
        TakePhotoTaskUpdate taskUpdate = new TakePhotoTaskUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (TakePhoto) tasksManager.getTask(taskUpdate);
    }

    @Override
    public TakePhotoInInterval takePhotoInInterval(int captureCount, int interval) throws DroneTaskException {
        return null;
    }
}
