package eyesatop.util;

import java.util.Collection;

public interface Removable {

    Removable STUB = new Removable() {@Override public void remove() {}};

    class Group implements Removable {

        private final Collection<? extends Removable> removables;

        public Group(Collection<? extends Removable> removables) {
            this.removables = removables;
        }

        @Override
        public void remove() {
            for (Removable removable : removables) {
                removable.remove();
            }
        }
    }

    void remove();
}
