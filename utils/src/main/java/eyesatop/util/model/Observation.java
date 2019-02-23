package eyesatop.util.model;

import java.util.Collection;

import eyesatop.util.Removable;

public interface Observation<T> extends Removable {

    class Group extends Removable.Group implements Observation {

        private final Collection<Observation> observations;

        public Group(Collection<Observation> observations) {
            super(observations);
            this.observations = observations;
        }

        @Override
        public Observation observeCurrentValue() {
            for (Observation observation : observations) {
                observation.observeCurrentValue();
            }
            return this;
        }

        @Override
        public Observer observer() {
            throw new UnsupportedOperationException("observer() on Observation.Group");
        }

        @Override
        public Observable observable() {
            throw new UnsupportedOperationException("observable() on Observation.Group");
        }
    }

    Observation<T> observeCurrentValue();
    Observer<T> observer();
    Observable<T> observable();
}
