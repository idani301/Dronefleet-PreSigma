package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaStorage {

    private static final String REMAINING_TIME = "remainingTime";
    private static final String REMAINING_BYTES = "remainingBytes";
    private static final String TOTAL_BYTES = "totalBytes";

    private final int remainingTime;
    private final long remainingSpaceInBytes;
    private final long totalSpaceInBytes;

    @JsonCreator
    public MediaStorage(
            @JsonProperty(REMAINING_TIME)
            int remainingTime,

            @JsonProperty(REMAINING_BYTES)
            long remainingSpaceInBytes,

            @JsonProperty(TOTAL_BYTES)
            long totalSpaceInBytes) {
        this.remainingTime = remainingTime;
        this.remainingSpaceInBytes = remainingSpaceInBytes;
        this.totalSpaceInBytes = totalSpaceInBytes;
    }

    @JsonProperty(REMAINING_TIME)
    public int getRemainingTime() {
        return remainingTime;
    }

    @JsonProperty(REMAINING_BYTES)
    public long getRemainingSpaceInBytes() {
        return remainingSpaceInBytes;
    }

    @JsonProperty(TOTAL_BYTES)
    public long getTotalSpaceInBytes() {
        return totalSpaceInBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaStorage that = (MediaStorage) o;

        if (remainingTime != that.remainingTime) return false;
        if (remainingSpaceInBytes != that.remainingSpaceInBytes) return false;
        return totalSpaceInBytes == that.totalSpaceInBytes;

    }

    @Override
    public int hashCode() {
        int result = remainingTime;
        result = 31 * result + (int) (remainingSpaceInBytes ^ (remainingSpaceInBytes >>> 32));
        result = 31 * result + (int) (totalSpaceInBytes ^ (totalSpaceInBytes >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "MediaStorage{" +
                "remainingTime=" + remainingTime +
                ", remainingSpaceInBytes=" + remainingSpaceInBytes +
                ", totalSpaceInBytes=" + totalSpaceInBytes +
                '}';
    }
}
