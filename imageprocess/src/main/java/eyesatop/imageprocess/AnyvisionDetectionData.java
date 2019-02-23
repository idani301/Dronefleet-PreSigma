package eyesatop.imageprocess;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Idan on 21/05/2018.
 */
// 05-21 12:25:07.650 13439-14343/mavlink.org.anyvisionintegrationapp I/System.out:
// {
// "_id":"5b029074c13a8e0fbb2c7880",
// "pipe":{
//      "_id":"5af94632a4c695d099fe84a6",
//      "remote_ip":"127.0.0.1",
//      "path":"\/pipe_store\/",
//      "storage_ip":"127.0.0.1",
//      "association_service_ip":"127.0.0.1"},
// "camera":{
//      "_id":"5b02905ec13a8e0fbb2c780c",
//      "title":"Berlin_Street_720p.mp4",
//      "camera_group_id":{
//          "_id":"000000000000000000000002",
//          "title":"Default Camera Group",
//          "map_path":"img\/map.jpg",
//          "camera_group_mode":"black",
//          "suspect_groups":[],"all_suspect_groups":true,"__v":0},"is_stills":false,"map_left_percent":null,"map_top_percent":null,"gpu_device_id":0},"face_uuid":"0YPI362JW8AO5YI6","directory":"tracks\/5b02905ec13a8e0fbb2c780c\/U9KN59PQ\/","pipe_id":"9863725e-03a4-4f25-bd3f-5c2c7215758b","frame_date":"2018-05-21T09:25:59.400Z","camera_type":"offline","collate_active":true,"threshold":0,"is_stills":false,"object_positions":[],"label":"unknown","class":"Face","created_date":"2018-05-21T09:25:08.698Z","close_matches":[],"suspects":[]}
//
    //
    //
    //

// 05-21 12:31:24.996 14780-14924/mavlink.org.anyvisionintegrationapp I/System.out:
// {
// "_id":"5b0291eec13a8e0fbb2c7ac3",
// "pipe":{
//      "_id":"5af94632a4c695d099fe84a6",
//      "remote_ip":"127.0.0.1",
//      "path":"\/pipe_store\/",
//      "storage_ip":"127.0.0.1",
//      "association_service_ip":"127.0.0.1"},
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
// "directory":"tracks\/5b0291c2c13a8e0fbb2c792f\/YCPD9J56\/",
// "pipe_id":"8a6a55f2-0156-46ed-a9c4-8faf447d3b52",
// "frame_date":"2018-05-21T09:31:49.400Z",
// "camera_type":"offline",
// "collate_active":true,
// "threshold":0,
// "is_stills":false,
// "object_positions":[],
// "label":"unknown",
// "class":"Person",
// "created_date":"2018-05-21T09:31:26.017Z",
// "close_matches":[],
// "suspects":[]}


public class AnyvisionDetectionData {

    private static final String ID = "_id";
    private static final String PIPE = "pipe";
    private static final String CAMERA = "camera";
    private static final String DIRECTORY = "directory";
    private static final String PIPE_ID= "pipe_id";
    private static final String FRAME_DATE = "frame_date";
    private static final String CAMERA_TYPE = "camera_type";
    private static final String COLLATE_ACTIVE = "collate_active";
    private static final String THRESHOLD = "threshold";
    private static final String IS_STILLS = "is_stills";
    private static final String OBJECT_POSITIONS = "object_positions";
    private static final String LABEL = "label";
    private static final String CLASS = "_class";
    private static final String CREATED_DATE= "created_date";
    private static final String CLOSE_MATCHES= "close_matches";
    private static final String SUSPECTS = "suspects";

    private final String _id;
    private final PipeData pipe;
    private final CameraData cameraData;
    private final String directory;
    private final String pipe_id;
    private final String frame_date;
    private final String camera_type;
    private final String collate_active;
    private final String threshold;
    private final String is_stills;
    private final String object_positions;
    private final String label;
    private final String _class;
    private final String created_date;
    private final String close_matches;
    private final String suspects;

    @JsonCreator
    public AnyvisionDetectionData(
            @JsonProperty(ID) String id,
            @JsonProperty(PIPE) PipeData pipe,
            @JsonProperty(CAMERA) CameraData cameraData,
            @JsonProperty(DIRECTORY) String directory,
            @JsonProperty(PIPE_ID) String pipe_id,
            @JsonProperty(FRAME_DATE) String frame_date,
            @JsonProperty(CAMERA_TYPE) String camera_type,
            @JsonProperty(COLLATE_ACTIVE) String collate_active,
            @JsonProperty(THRESHOLD) String threshold,
            @JsonProperty(IS_STILLS) String is_stills,
            @JsonProperty(OBJECT_POSITIONS) String object_positions,
            @JsonProperty(LABEL) String label,
            @JsonProperty (CLASS) String aClass,
            @JsonProperty(CREATED_DATE) String created_date,
            @JsonProperty(CLOSE_MATCHES) String close_matches,
            @JsonProperty(SUSPECTS) String suspects) {
        _id = id;
        this.pipe = pipe;
        this.cameraData = cameraData;
        this.directory = directory;
        this.pipe_id = pipe_id;
        this.frame_date = frame_date;
        this.camera_type = camera_type;
        this.collate_active = collate_active;
        this.threshold = threshold;
        this.is_stills = is_stills;
        this.object_positions = object_positions;
        this.label = label;
        _class = aClass;
        this.created_date = created_date;
        this.close_matches = close_matches;
        this.suspects = suspects;
    }

    @JsonProperty(ID)
    public String get_id() {
        return _id;
    }

    @JsonProperty(PIPE)
    public PipeData getPipe() {
        return pipe;
    }

    @JsonProperty(CAMERA)
    public CameraData getCameraData() {
        return cameraData;
    }

    @JsonProperty(DIRECTORY)
    public String getDirectory() {
        return directory;
    }

    @JsonProperty(PIPE_ID)
    public String getPipe_id() {
        return pipe_id;
    }

    @JsonProperty(FRAME_DATE)
    public String getFrame_date() {
        return frame_date;
    }

    @JsonProperty(CAMERA_TYPE)
    public String getCamera_type() {
        return camera_type;
    }

    @JsonProperty(COLLATE_ACTIVE)
    public String getCollate_active() {
        return collate_active;
    }

    @JsonProperty(THRESHOLD)
    public String getThreshold() {
        return threshold;
    }

    @JsonProperty(IS_STILLS)
    public String getIs_stills() {
        return is_stills;
    }

    @JsonProperty(OBJECT_POSITIONS)
    public String getObject_positions() {
        return object_positions;
    }

    @JsonProperty(LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(CLASS)
    public String get_class() {
        return _class;
    }

    @JsonProperty(CREATED_DATE)
    public String getCreated_date() {
        return created_date;
    }

    @JsonProperty(CLOSE_MATCHES)
    public String getClose_matches() {
        return close_matches;
    }

    @JsonProperty(SUSPECTS)
    public String getSuspects() {
        return suspects;
    }
}
