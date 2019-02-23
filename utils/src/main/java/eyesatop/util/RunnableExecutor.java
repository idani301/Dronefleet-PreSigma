package eyesatop.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class RunnableExecutor implements Executor, Runnable {

    private final Collection<Runnable> runnables = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        for (Runnable runnable : runnables) {
            runnables.remove(runnable);
            runnable.run();
        }
    }

    @Override
    public void execute(Runnable command) {
        runnables.add(command);
    }

    public Collection<Runnable> getRunnables() {
        return new LinkedList<>(runnables);
    }
}
