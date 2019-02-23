package eyesatop.util.model.predicates;

import eyesatop.util.Predicate;

/**
 * Created by einav on 21/02/2017.
 */

public class NotNull<T> implements Predicate<T> {
    @Override
    public boolean test(Object subject) {
        return subject != null;
    }
}
