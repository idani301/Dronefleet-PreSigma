package eyesatop.util.model.functions;

import java.util.List;

import eyesatop.util.Function;

public class ItemAtIndexOfList<T> implements Function<Integer, T> {

    private final List<T> list;

    public ItemAtIndexOfList(List<T> list) {
        this.list = list;
    }

    @Override
    public T apply(Integer input) {
        return list.get(input);
    }
}
