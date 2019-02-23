package eyesatop.util.android;


import android.util.Log;

import eyesatop.util.Logger;

public class AndroidLogger implements Logger {

    private final String tag;

    public AndroidLogger(Class cls) {
        this(cls.getSimpleName());
    }

    public AndroidLogger(String tag) {
        this.tag = tag;
    }

    @Override
    public void debug(String str) {
        Log.d(tag, str);
    }
}
