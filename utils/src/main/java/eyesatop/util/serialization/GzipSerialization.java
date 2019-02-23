package eyesatop.util.serialization;

//import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipSerialization implements Serialization {

    private final Serialization delegate;

    public GzipSerialization(Serialization delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getMimeType() {
        return delegate.getMimeType() + "+gzip";
    }

    @Override
    public String serialize(Object o) throws IOException {
        throw noText();
    }

    @Override
    public void serialize(Object o, OutputStream out) throws IOException {
        out = new GZIPOutputStream(out);
        delegate.serialize(o, out);
        out.flush();
    }

    @Override
    public <T> T deserialize(String s, Type type) throws IOException {
        throw noText();
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Type type) throws IOException {
        return delegate.deserialize(new GZIPInputStream(inputStream), type);
    }

    private UnsupportedOperationException noText() {
        return new UnsupportedOperationException("gzip serialization doesn't work with text");
    }
}
