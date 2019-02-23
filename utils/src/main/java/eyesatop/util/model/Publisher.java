package eyesatop.util.model;

public interface Publisher<T> {
    int publish(T oldValue, T newValue);
}
