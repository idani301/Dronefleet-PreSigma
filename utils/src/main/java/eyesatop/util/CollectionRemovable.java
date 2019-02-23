package eyesatop.util;

import java.util.Collection;

public class CollectionRemovable<T> implements Removable {

    private final Collection<T> collection;
    private final T item;

    public CollectionRemovable(Collection<T> collection, T item) {
        this.collection = collection;
        this.item = item;
    }

    @Override
    public void remove() {
        collection.remove(item);
    }
}
