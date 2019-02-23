package eyesatop.util.model;

import java.util.concurrent.TimeUnit;

import eyesatop.util.BlockingExecutor;
import eyesatop.util.Predicate;
import eyesatop.util.PropertyMaybe;
import eyesatop.util.model.functions.DefaultOnNull;
import eyesatop.util.model.functions.Toggle;
import eyesatop.util.model.predicates.EqualsPredicate;

public class BooleanProperty extends Property<Boolean> implements ObservableBoolean {

    private interface BooleanCalculator {
        boolean calculate(boolean first, boolean second);
    }

    private static class BooleanCalculatorSettingObserver implements Observer<Boolean> {
        private final Valued<Boolean> firstBoolean;
        private final Valued<Boolean> secondBoolean;
        private final PropertyMaybe<Boolean> propertyMaybe;
        private final BooleanCalculator calculator;

        BooleanCalculatorSettingObserver(
                Valued<Boolean> firstBoolean,
                Valued<Boolean> secondBoolean,
                Property<Boolean> property,
                BooleanCalculator calculator) {
            this.firstBoolean = firstBoolean;
            this.secondBoolean = secondBoolean;
            this.propertyMaybe = new PropertyMaybe<>(property);
            this.calculator = calculator;
        }

        public boolean valueOf(Valued<Boolean> valued) {
            return valued.value() != null && valued.value();
        }

        @Override
        public final void observe(Boolean oldValue, Boolean newValue, Observation observation) {
            Settable<Boolean> settable = propertyMaybe.getProperty();
            if (settable == null) {
                observation.remove();
            } else {
                settable.set(calculator.calculate(
                        valueOf(firstBoolean),
                        valueOf(secondBoolean)
                ));
                propertyMaybe.invalidate();
            }
        }
    }

    public BooleanProperty() {
        this(null);
    }

    public BooleanProperty(Boolean value) {
        super(value);
    }


    @Override
    public ObservableBoolean or(ObservableValue<Boolean> other) {
        return combine(this, other, new BooleanCalculator() {
            @Override
            public boolean calculate(boolean first, boolean second) {
                return first || second;
            }
        });
    }

    @Override
    public ObservableBoolean and(ObservableValue<Boolean> other) {
        return combine(this, other, new BooleanCalculator() {
            @Override
            public boolean calculate(boolean first, boolean second) {
                return first && second;
            }
        });
    }

    @Override
    public ObservableBoolean not() {
        return combine(this, this, new BooleanCalculator() {
            @Override
            public boolean calculate(boolean first, boolean second) {
                return !first;
            }
        });
    }

    @Override
    public <T> ObservableValue<T> toggle(T enabled, T disabled) {
        return transform(new Toggle<T>(enabled, disabled));
    }

    @Override
    public void awaitTrue() throws InterruptedException {
        await(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean subject) {
                return subject != null && subject;
            }
        });
    }

    @Override
    public boolean awaitTrue(int timeout, TimeUnit timeUnit) throws InterruptedException {
        await(new EqualsPredicate<>(true), timeout, timeUnit);
        return value();
    }

    @Override
    public void awaitFalse() throws InterruptedException {
        await(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean subject) {
                return subject != null && !subject;
            }
        });
    }

    @Override
    public boolean awaitFalse(int timeout, TimeUnit timeUnit) throws InterruptedException {
        await(new EqualsPredicate<>(true), timeout, timeUnit);
        return !value();
    }

    //
//    @Override
//    public Boolean value() {
//        return super.value() == null ? false : super.value();
//    }

    private ObservableBoolean combine(
            final ObservableValue<Boolean> first,
            final ObservableValue<Boolean> second,
            final BooleanCalculator calculator) {
        BooleanProperty property = new BooleanProperty(calculator.calculate(
                first.value() == null ? false : first.value(),
                second.value() == null ? false : second.value()));
        Observer<Boolean> observer = new BooleanCalculatorSettingObserver(first, second, property, calculator);
        first.observe(observer);
        if (first != second) {
            second.observe(observer);
        }
        return property;
    }
}
