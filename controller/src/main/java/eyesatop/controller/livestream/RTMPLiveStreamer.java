package eyesatop.controller.livestream;

import eyesatop.controller.beans.StreamState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.ObservableValue;

public interface RTMPLiveStreamer {

    ObservableValue<StreamState> streamState();

    void startStream(String url) throws DroneTaskException;
    void stopStream() throws DroneTaskException;
}
