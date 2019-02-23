package eyesatop.util.model;

public interface Valued<T> {
    T value();
    boolean is(T value);
    boolean isOfType(Class type);
    boolean isNull();
}
