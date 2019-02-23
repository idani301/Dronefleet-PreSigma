package eyesatop.util.model;

import java.util.Collection;
import java.util.Iterator;

public class CollectionPublisher<T> implements Publisher<T> {

    private final Valued<T> valued;
    private final Collection<Observation<T>> observations;

    public CollectionPublisher(Valued<T> valued, Collection<Observation<T>> observations) {
        this.valued = valued;
        this.observations = observations;
    }

    @Override
    public int publish(T oldValue, T newValue) {
        Iterator<Observation<T>> iter = observations.iterator();
        int count = 0;
        while (iter.hasNext()) {
            Observation<T> observation = iter.next();
            observation.observer().observe(
                    oldValue, newValue, observation);
            count ++;
        }
        return count;
    }
}
