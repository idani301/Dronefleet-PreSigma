package eyesatop.util;

public interface ThrowableHandler<E extends Throwable> {

    class PrintStackTrace<E extends Throwable> implements ThrowableHandler<E> {
        @Override
        public void handle(Throwable t) {
            t.printStackTrace();
        }
    }



    void handle(Throwable t);
}
