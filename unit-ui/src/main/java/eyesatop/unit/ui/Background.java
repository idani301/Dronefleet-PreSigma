package eyesatop.unit.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;

import eyesatop.util.Function;

public interface Background {
    class Resolver implements Function<Background, Drawable> {
        private final Context context;

        public Resolver(Context context) {
            this.context = context;
        }

        @Override
        public Drawable apply(Background input) {
            return input.resolve(context);
        }
    }

    Drawable resolve(Context context);
}
