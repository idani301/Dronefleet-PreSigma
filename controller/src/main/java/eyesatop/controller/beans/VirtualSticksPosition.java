package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VirtualSticksPosition {

    private static final String STICKS_POSITION = "sticksPosition";
    private static final String CREATION_TIME = "creationTime";

    private final SticksPosition sticksPosition;
    private final long creationTime;

    @JsonCreator
    public VirtualSticksPosition(@JsonProperty(STICKS_POSITION) SticksPosition sticksPosition,
                                 @JsonProperty(CREATION_TIME) long creationTime) {
        this.sticksPosition = sticksPosition;
        this.creationTime = creationTime;
    }

    @JsonProperty(STICKS_POSITION)
    public SticksPosition getSticksPosition() {
        return sticksPosition;
    }

    @JsonProperty(CREATION_TIME)
    public long getCreationTime() {
        return creationTime;
    }

    @JsonIgnore
    public boolean isRelevant(final long sticksValidDuration) {

        if(System.currentTimeMillis() - creationTime > sticksValidDuration){
            return false;
        }

        if(sticksPosition == null){
            return false;
        }

        return sticksPosition.isRelevant();
    }
}
