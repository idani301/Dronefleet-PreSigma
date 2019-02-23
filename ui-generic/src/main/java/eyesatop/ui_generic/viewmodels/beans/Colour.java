package eyesatop.ui_generic.viewmodels.beans;

import android.content.Context;

import eyesatop.util.Function;

public interface Colour {
    Function<Integer,Colour> WRAP_VALUE = new Function<Integer, Colour>() {
        @Override
        public Colour apply(Integer input) {
            return new ColourValue(input);
        }
    };

    Function<Integer,Colour> WRAP_ID = new Function<Integer, Colour>() {
        @Override
        public Colour apply(Integer input) {
            return new ColourId(input);
        }
    };

    int resolve(Context context);
}
