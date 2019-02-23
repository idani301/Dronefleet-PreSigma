package eyesatop.util;

public interface RequestListener<T> {
    void success(T response);
    void failure(Throwable t);
}
