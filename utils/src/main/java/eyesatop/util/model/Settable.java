package eyesatop.util.model;

public interface Settable<T> {
    Settable STUB = new Settable() {@Override public void set(Object value) {}};

    void set(T value);
}
