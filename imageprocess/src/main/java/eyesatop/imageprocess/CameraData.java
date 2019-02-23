package eyesatop.imageprocess;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Idan on 21/05/2018.
 */
// "camera":{
//      "_id":"5b0291c2c13a8e0fbb2c792f",
//      "title":"Berlin_Street_720p.mp4",
//      "camera_group_id":{
//          "_id":"000000000000000000000002",
//          "title":"Default Camera Group",
//          "map_path":"img\/map.jpg",
//          "camera_group_mode":"black",
//          "suspect_groups":[],
//          "all_suspect_groups":true,
//          "__v":0},
//      "is_stills":false,
//      "map_left_percent":null,
//      "map_top_percent":null,
//      "gpu_device_id":0},

public class CameraData {

    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String GROUP_ID = "camera_group_id";
    private static final String IS_STILLS = "is_stills";
    private static final String MAP_LEFT = "map_left_percent";
    private static final String MAP_TOP = "map_top_percent";
    private static final String GPU_DEVICE = "gpu_device_id";

    private final String _id;
    private final String title;
    private final CameraGroupIDData camera_group_id;
    private final String is_stills;
    private final String map_left_percent;
    private final String map_top_percent;
    private final String gpu_device_id;

    @JsonCreator
    public CameraData(
            @JsonProperty(ID) String id,
            @JsonProperty(TITLE) String title,
            @JsonProperty(GROUP_ID) CameraGroupIDData camera_group_id,
            @JsonProperty(IS_STILLS) String is_stills,
            @JsonProperty(MAP_LEFT) String map_left_percent,
            @JsonProperty(MAP_TOP) String map_top_percent,
            @JsonProperty(GPU_DEVICE) String gpu_device_id) {
        _id = id;
        this.title = title;
        this.camera_group_id = camera_group_id;
        this.is_stills = is_stills;
        this.map_left_percent = map_left_percent;
        this.map_top_percent = map_top_percent;
        this.gpu_device_id = gpu_device_id;
    }

    @JsonProperty(ID)
    public String get_id() {
        return _id;
    }

    @JsonProperty(TITLE)
    public String getTitle() {
        return title;
    }

    @JsonProperty(GROUP_ID)
    public CameraGroupIDData getCamera_group_id() {
        return camera_group_id;
    }

    @JsonProperty(IS_STILLS)
    public String getIs_stills() {
        return is_stills;
    }

    @JsonProperty(MAP_LEFT)
    public String getMap_left_percent() {
        return map_left_percent;
    }

    @JsonProperty(MAP_TOP)
    public String getMap_top_percent() {
        return map_top_percent;
    }

    @JsonProperty(GPU_DEVICE)
    public String getGpu_device_id() {
        return gpu_device_id;
    }
}
