package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class StreamState {

    private static final String IS_STREAMING = "isStreaming";
    private static final String URL = "streamURL";
    private static final String VIDEO_BIT_RATE = "videoBitRate";

    private final boolean isStreaming;
    private final String streamURL;
    private final int videoBitRate;

    @JsonCreator
    public StreamState(@JsonProperty(IS_STREAMING) boolean isStreaming,
                       @JsonProperty(URL) String streamURL,
                       @JsonProperty(VIDEO_BIT_RATE) int videoBitRate) {
        this.isStreaming = isStreaming;
        this.streamURL = streamURL;
        this.videoBitRate = videoBitRate;
    }

    @JsonProperty(IS_STREAMING)
    public boolean isStreaming() {
        return isStreaming;
    }

    @JsonProperty(URL)
    public String getStreamURL() {
        return streamURL;
    }

    @JsonProperty(VIDEO_BIT_RATE)
    public int getVideoBitRate() {
        return videoBitRate;
    }
}
