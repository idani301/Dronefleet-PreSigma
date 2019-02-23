package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;

public class VideoPacket implements Serializable {
    private static final String CODEC = "codec";
    private static final String DATA = "data";
    private static final String SIZE = "size";
    private static final String INTERVAL = "interval";

    private final VideoCodecType codecType;
    private final byte[] data;
    private final long interval;

    private final int size;

    @JsonCreator
    public VideoPacket(
            @JsonProperty(CODEC)
            VideoCodecType codecType,

            @JsonProperty(DATA)
            byte[] data,

            @JsonProperty(SIZE)
            int size) {
        this.codecType = codecType;
        this.data = data;
        this.size = size;
        this.interval = 0;
    }

    public VideoPacket(VideoCodecType codecType, byte[] data, long interval, int size) {
        this.codecType = codecType;
        this.data = data;
        this.interval = interval;
        this.size = size;
    }

    @JsonProperty(INTERVAL)
    public long getInterval() {
        return interval;
    }

    @JsonProperty(SIZE)
    public int getSize() {
        return size;
    }

    @JsonProperty(CODEC)
    public VideoCodecType getCodecType() {
        return codecType;
    }

    @JsonProperty(DATA)
    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoPacket that = (VideoPacket) o;

        if (codecType != that.codecType) return false;
        return Arrays.equals(data, that.data);

    }

    @Override
    public int hashCode() {
        int result = codecType != null ? codecType.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
