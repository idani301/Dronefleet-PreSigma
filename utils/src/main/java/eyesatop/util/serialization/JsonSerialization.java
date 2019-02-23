package eyesatop.util.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

//import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JsonSerialization implements Serialization {

    private transient final ObjectMapper objectMapper;

    public JsonSerialization() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.findAndRegisterModules();
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public String serialize(Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serialize(o, baos);
        return baos.toString();
    }

    @Override
    public void serialize(Object o, OutputStream out) throws IOException {
        objectMapper.writeValue(out, o);
    }

    @Override
    public <T> T deserialize(String s, Type type) throws IOException {
        return deserialize(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)), type);
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Type type) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
        int b = pushbackInputStream.read();
        if (b == -1) {
            return null;
        }
        pushbackInputStream.unread(b);
        return objectMapper.readValue(pushbackInputStream, objectMapper.constructType(type));
    }
}
