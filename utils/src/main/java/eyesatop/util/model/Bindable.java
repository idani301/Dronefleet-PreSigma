package eyesatop.util.model;

public interface Bindable<T> {
    Observation<T> bind(Observable<T> observable);
}
