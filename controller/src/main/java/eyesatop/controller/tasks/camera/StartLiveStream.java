package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

public interface StartLiveStream extends DroneTask<CameraTaskType> {
    String url();

    public class StartLiveStreamStub extends StubDroneTask<CameraTaskType> implements StartLiveStream {

        private final String url;

        public StartLiveStreamStub(String url) {
            this.url = url;
        }


        @Override
        public String url() {
            return url;
        }

        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.START_LIVE_STREAM;
        }
    }
}
