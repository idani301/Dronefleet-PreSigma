package eyesatop.util.model.functions;

import java.io.IOException;

import eyesatop.util.Function;
import eyesatop.util.serialization.Serialization;

public class SerializedForm<T> implements Function<T,String> {

    private final Serialization serialization;

    public SerializedForm(Serialization serialization) {
        this.serialization = serialization;
    }

    @Override
    public String apply(T input) {
        try {
            return serialization.serialize(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
