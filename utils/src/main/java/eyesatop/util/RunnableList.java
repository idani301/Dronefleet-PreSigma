package eyesatop.util;

import java.util.List;

public class RunnableList implements Runnable {

    private final List<Runnable> runnables;

    public RunnableList(List<Runnable> runnables) {
        this.runnables = runnables;
    }

    @Override
    public void run() {
        System.out.println("Connecting...");
        for (Runnable runnable : runnables) {
            runnable.run();
        }
    }
}
