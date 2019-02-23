package eyesatop.controller_tcpip.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RemoteBroadcastInfo {
    private static final String IP = "ip";
    private final String ip;

    @JsonCreator
    public RemoteBroadcastInfo(@JsonProperty(IP) String ip) {
        this.ip = ip;
    }

    @JsonProperty(IP)
    public String getIp() {
        return ip;
    }
}
