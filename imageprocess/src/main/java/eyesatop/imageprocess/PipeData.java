package eyesatop.imageprocess;
//
///**
// * Created by Idan on 21/05/2018.
// */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// "pipe":{
//      "_id":"5af94632a4c695d099fe84a6",
//      "remote_ip":"127.0.0.1",
//      "path":"\/pipe_store\/",
//      "storage_ip":"127.0.0.1",
//      "association_service_ip":"127.0.0.1"},
//
public class PipeData {

    private static final String ID = "_id";
    private static final String REMOTE_IP = "remote_ip";
    private static final String PATH = "path";
    private static final String STORAGE_IP = "storage_ip";
    private static final String ASSOCIATION_SERVICE_IP = "association_service_ip";

    private final String _id;
    private final String remote_ip;
    private final String path;
    private final String storage_ip;
    private final String association_service_ip;

    @JsonCreator
    public PipeData(
            @JsonProperty(ID) String id,
            @JsonProperty(REMOTE_IP) String remote_ip,
            @JsonProperty(PATH) String path,
            @JsonProperty(STORAGE_IP) String storage_ip,
            @JsonProperty(ASSOCIATION_SERVICE_IP) String association_service_ip) {
        _id = id;
        this.remote_ip = remote_ip;
        this.path = path;
        this.storage_ip = storage_ip;
        this.association_service_ip = association_service_ip;
    }

    @JsonProperty(ID)
    public String get_id() {
        return _id;
    }

    @JsonProperty(REMOTE_IP)
    public String getRemote_ip() {
        return remote_ip;
    }

    @JsonProperty(PATH)
    public String getPath() {
        return path;
    }

    @JsonProperty(STORAGE_IP)
    public String getStorage_ip() {
        return storage_ip;
    }

    @JsonProperty(ASSOCIATION_SERVICE_IP)
    public String getAssociation_service_ip() {
        return association_service_ip;
    }
}
