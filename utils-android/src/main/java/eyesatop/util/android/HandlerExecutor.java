package eyesatop.util.android;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by einav on 13/04/2017.
 */

public class HandlerExecutor implements Executor {

    public static final Executor MAIN_LOOPER_EXECUTOR = new HandlerExecutor(new Handler(Looper.getMainLooper()));

    private final Handler handler;

    private HandlerExecutor(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }
}
