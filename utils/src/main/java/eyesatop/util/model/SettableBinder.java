package eyesatop.util.model;

public class SettableBinder<T> implements Bindable<T> {

    private final Settable<T> settable;

    public SettableBinder(Settable<T> settable) {
        this.settable = settable;
    }

    @Override
    public Observation<T> bind(Observable<T> observable) {
        return observable.observe(new BindingObserver<>(settable))
                    .observeCurrentValue();
    }
}
