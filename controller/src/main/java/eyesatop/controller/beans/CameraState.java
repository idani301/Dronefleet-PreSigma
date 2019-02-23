package eyesatop.controller.beans;

/**
 * Created by einav on 24/01/2017.
 */
public class CameraState {
    private final CameraMode cameraMode;
    private final boolean recording;
    private final boolean locked;
    private final int remainingRecordTime;
    private final int sdRemainingSpaceInBytes;
    private final int sdTotalSpaceInBytes;

    public CameraState(CameraMode cameraMode, boolean isRecording, boolean isLocked, int remainingRecordTime, int sdRemainingSpaceInBytes, int sdTotalSpaceInBytes) {
        this.cameraMode = cameraMode;
        this.recording = isRecording;
        this.locked = isLocked;
        this.remainingRecordTime = remainingRecordTime;
        this.sdRemainingSpaceInBytes = sdRemainingSpaceInBytes;
        this.sdTotalSpaceInBytes = sdTotalSpaceInBytes;
    }

    public boolean isLocked() {
        return locked;
    }

    public CameraState cameraMode(CameraMode cameraMode){
        return new CameraState(cameraMode, recording, locked, remainingRecordTime,sdRemainingSpaceInBytes,sdTotalSpaceInBytes);
    }

    public CameraState isRecording(boolean isRecording){
        return new CameraState(cameraMode,isRecording, locked, remainingRecordTime,sdRemainingSpaceInBytes,sdTotalSpaceInBytes);
    }

    public CameraState locked(boolean isLocked){
        return new CameraState(cameraMode, recording, isLocked, remainingRecordTime,sdRemainingSpaceInBytes,sdTotalSpaceInBytes);
    }

    public CameraState remainingRecordTime(int remainingRecordTime){
        return new CameraState(cameraMode, recording, locked, remainingRecordTime,sdRemainingSpaceInBytes,sdTotalSpaceInBytes);
    }

    public CameraState sdRemainingSpaceInBytes(int sdRemainingSpaceInBytes){
        return new CameraState(cameraMode, recording, locked, remainingRecordTime,sdRemainingSpaceInBytes,sdTotalSpaceInBytes);
    }

    public CameraState sdTotalSpaceInBytes(int sdTotalSpaceInBytes){
        return new CameraState(cameraMode, recording, locked, remainingRecordTime,sdRemainingSpaceInBytes,sdTotalSpaceInBytes);
    }

    @Override
    public String toString() {
        return "CameraState{" +
                "cameraMode=" + cameraMode +
                ", recording=" + recording +
                ", remainingRecordTime=" + remainingRecordTime +
                ", sdRemainingSpaceInBytes=" + sdRemainingSpaceInBytes +
                ", sdTotalSpaceInBytes=" + sdTotalSpaceInBytes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CameraState that = (CameraState) o;

        if (recording != that.recording) return false;
        if (remainingRecordTime != that.remainingRecordTime) return false;
        if (sdRemainingSpaceInBytes != that.sdRemainingSpaceInBytes) return false;
        if (sdTotalSpaceInBytes != that.sdTotalSpaceInBytes) return false;
        return cameraMode == that.cameraMode;

    }

    @Override
    public int hashCode() {
        int result = cameraMode != null ? cameraMode.hashCode() : 0;
        result = 31 * result + (recording ? 1 : 0);
        result = 31 * result + remainingRecordTime;
        result = 31 * result + sdRemainingSpaceInBytes;
        result = 31 * result + sdTotalSpaceInBytes;
        return result;
    }

    public CameraMode getCameraMode() {
        return cameraMode;
    }

    public boolean isRecording() {
        return recording;
    }

    public int getRemainingRecordTime() {
        return remainingRecordTime;
    }

    public int getSdRemainingSpaceInBytes() {
        return sdRemainingSpaceInBytes;
    }

    public int getSdTotalSpaceInBytes() {
        return sdTotalSpaceInBytes;
    }
}
