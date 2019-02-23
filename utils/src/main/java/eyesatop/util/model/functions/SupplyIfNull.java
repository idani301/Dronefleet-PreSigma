package eyesatop.util.model.functions;

import eyesatop.util.Function;
import eyesatop.util.model.Valued;

public class SupplyIfNull<T> implements Function<T,T> {
    private final Valued<T> valued;

    public SupplyIfNull(Valued<T> valued) {
        this.valued = valued;
    }

    @Override
    public T apply(T input) {
        return input == null ? valued.value() : input;
    }
}
