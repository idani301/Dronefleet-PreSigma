package eyesatop.util;

import java.lang.ref.WeakReference;

import eyesatop.util.model.Property;

public class PropertyMaybe<T> {
    private final WeakReference<Property<T>> weakProperty;
    private Property<T> strongProperty;

    public PropertyMaybe(Property<T> property) {
        this.weakProperty = new WeakReference<Property<T>>(property);
        this.strongProperty = property;
    }

    public Property<T> getProperty() {
        return weakProperty.get();
    }

    public void invalidate() {
        Property<T> property = weakProperty.get();
        if (property != null && property.observations().size() > 0) {
            strongProperty = property;
        } else {
            strongProperty = null;
        }
    }
}
