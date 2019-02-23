package eyesatop.util.model.predicates;

import eyesatop.util.Predicate;

public class EqualsPredicate<T> implements Predicate<T> {
    private final T standard;

    public EqualsPredicate(T standard) {
        this.standard = standard;
    }

    @Override
    public boolean test(T subject) {
        return (standard == null) ? (subject == null) : standard.equals(subject);
    }
}
