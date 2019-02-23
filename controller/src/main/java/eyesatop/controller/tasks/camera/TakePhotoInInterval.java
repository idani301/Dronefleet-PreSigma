package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 09/10/2017.
 */

public interface TakePhotoInInterval {
    int captureCount();
    int interval();

    public class StubTakePhotoInInterval extends StubDroneTask<CameraTaskType> implements TakePhotoInInterval{

        private final int captureCount;
        private final int interval;

        public StubTakePhotoInInterval(int captureCount, int interval) {
            this.captureCount = captureCount;
            this.interval = interval;
        }

        @Override
        public int captureCount() {
            return captureCount;
        }

        @Override
        public int interval() {
            return interval;
        }

        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.TAKE_PHOTO_INTERVAL;
        }
    }
}
