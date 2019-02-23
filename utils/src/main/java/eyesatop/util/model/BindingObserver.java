package eyesatop.util.model;

public class BindingObserver<T> implements Observer<T> {

    private final Settable<T> settable;

    public BindingObserver(Settable<T> settable) {
        this.settable = settable;
    }

    @Override
    public void observe(T oldValue, T newValue, Observation<T> observation) {
        settable.set(newValue);
    }
}
