package eyesatop.util.model;

public class CollectionObserver<T> implements Observer<T> {
    @Override
    public void observe(T oldValue, T newValue, Observation<T> observation) {
        if (oldValue == null && newValue != null) {
            added(newValue, observation);
        } else if (oldValue != null && newValue == null) {
            removed(oldValue, observation);
        } else if (oldValue != null) {
            replaced(oldValue, newValue, observation);
        }
    }

    public void added(T value, Observation<T> observation) {}
    public void removed(T value, Observation<T> observation) {}
    public void replaced(T oldValue, T newValue, Observation<T> observation) {}
}
