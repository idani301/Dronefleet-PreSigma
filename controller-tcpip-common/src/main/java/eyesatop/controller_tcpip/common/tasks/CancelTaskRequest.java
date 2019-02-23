package eyesatop.controller_tcpip.common.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class CancelTaskRequest {

    private static final String UUID = "uuid";

    private final UUID uuid;

    @JsonCreator
    public CancelTaskRequest(@JsonProperty(UUID) UUID uuid) {
        this.uuid = uuid;
    }

    @JsonProperty(UUID)
    public java.util.UUID getUuid() {
        return uuid;
    }
}
