package eyesatop.util.model.functions;

import eyesatop.util.Function;

public class FormatString<T> implements Function<T, String> {

    private final String format;

    public FormatString(String format) {
        this.format = format;
    }

    @Override
    public String apply(T input) {
        if (input == null) {
            return null;
        }
        return String.format(format, input);
    }
}
