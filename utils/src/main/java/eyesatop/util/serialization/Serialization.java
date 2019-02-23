package eyesatop.util.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

//import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

@JsonTypeInfo(
        use=JsonTypeInfo.Id.MINIMAL_CLASS,
        include=JsonTypeInfo.As.PROPERTY,
        property="@class"
)
public interface Serialization {

    Serialization JSON = new JsonSerialization();
    Serialization JSON_GZIP = new GzipSerialization(JSON);

    @JsonIgnore
    String getMimeType();

    String serialize(Object o) throws IOException;
    void serialize(Object o, OutputStream out) throws IOException;
    <T> T deserialize(String s, Type type) throws IOException;
    <T> T deserialize(InputStream inputStream, Type type) throws IOException;
}
