package eyesatop.controller_tcpip.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PipeBroadcastInfo {

    private static final String DRONE_ID = "droneID";
    private static final String IP = "ip";

    private final int droneID;
    private final String ip;

    @JsonCreator
    public PipeBroadcastInfo(@JsonProperty(DRONE_ID) int droneID,
                             @JsonProperty(IP) String ip) {
        this.droneID = droneID;
        this.ip = ip;
    }

    @JsonProperty(DRONE_ID)
    public int getDroneID() {
        return droneID;
    }

    @JsonProperty(IP)
    public String getIp() {
        return ip;
    }
}
