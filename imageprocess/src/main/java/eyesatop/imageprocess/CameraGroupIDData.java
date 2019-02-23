package eyesatop.imageprocess;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Idan on 21/05/2018.
 */

//      "camera_group_id":{
//          "_id":"000000000000000000000002",
//          "title":"Default Camera Group",
//          "map_path":"img\/map.jpg",
//          "camera_group_mode":"black",
//          "suspect_groups":[],
//          "all_suspect_groups":true,
//          "__v":0},

public class CameraGroupIDData {

    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String PATH = "map_path";
    private static final String CAMERA_GROUP = "camera_group_mode";
    private static final String SUSPECT_GROUP = "suspect_groups";
    private static final String ALL_SUSPECT_GROUP = "all_suspect_groups";
    private static final String V = "__v";

    private final String _id;
    private final String title;
    private final String map_path;
    private final String camera_group_mode;
    private final String suspect_groups;
    private final String all_suspect_groups;
    private final String __v;

    @JsonCreator
    public CameraGroupIDData(
            @JsonProperty(ID) String id,
            @JsonProperty(TITLE) String title,
            @JsonProperty(PATH) String map_path,
            @JsonProperty(CAMERA_GROUP) String camera_group_mode,
            @JsonProperty(SUSPECT_GROUP) String suspect_groups,
            @JsonProperty(ALL_SUSPECT_GROUP) String all_suspect_groups,
            @JsonProperty(V) String v) {
        _id = id;
        this.title = title;
        this.map_path = map_path;
        this.camera_group_mode = camera_group_mode;
        this.suspect_groups = suspect_groups;
        this.all_suspect_groups = all_suspect_groups;
        __v = v;
    }

    @JsonProperty(ID)
    public String get_id() {
        return _id;
    }

    @JsonProperty(TITLE)
    public String getTitle() {
        return title;
    }

    @JsonProperty(PATH)
    public String getMap_path() {
        return map_path;
    }

    @JsonProperty(CAMERA_GROUP)
    public String getCamera_group_mode() {
        return camera_group_mode;
    }

    @JsonProperty(SUSPECT_GROUP)
    public String getSuspect_groups() {
        return suspect_groups;
    }

    @JsonProperty(ALL_SUSPECT_GROUP)
    public String getAll_suspect_groups() {
        return all_suspect_groups;
    }

    @JsonProperty(V)
    public String get__v() {
        return __v;
    }
}
