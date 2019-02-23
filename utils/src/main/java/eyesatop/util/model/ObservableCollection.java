package eyesatop.util.model;

import java.util.Collection;

import eyesatop.util.Predicate;

public interface ObservableCollection<T> extends Collection<T>, ObservableValue<T> {
    T lastValue();
    ObservableCollection<T> unmodifiable();
    int removeAll(Predicate<T> predicate);
}
