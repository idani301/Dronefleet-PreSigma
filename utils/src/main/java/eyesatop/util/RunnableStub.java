package eyesatop.util;

public class RunnableStub implements Runnable {

    public static final Runnable INSTANCE = new RunnableStub();

    private RunnableStub() {}

    @Override
    public void run() {}
}
