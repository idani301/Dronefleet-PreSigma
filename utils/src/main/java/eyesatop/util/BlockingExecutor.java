package eyesatop.util;

import java.util.concurrent.Executor;

public class BlockingExecutor implements Executor {

    public static final Executor INSTANCE = new BlockingExecutor();

    @Override
    public void execute(Runnable command) {
        try {
            command.run();
        }catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
