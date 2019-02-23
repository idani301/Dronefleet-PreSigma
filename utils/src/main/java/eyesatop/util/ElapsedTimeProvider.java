package eyesatop.util;

import java.util.concurrent.TimeUnit;

public class ElapsedTimeProvider implements TimeProvider {

    public static TimeProvider fromNow() {
        return from(0, TimeUnit.NANOSECONDS);
    }

    public static TimeProvider from(int timeFromNow, TimeUnit timeUnit) {
        return new ElapsedTimeProvider(System.nanoTime() + timeUnit.toNanos(timeFromNow));
    }

    private final long started;

    private ElapsedTimeProvider(long started) {
        this.started = started;
    }

    @Override
    public Time current() {
        return new Time(System.nanoTime()-started, TimeUnit.NANOSECONDS);
    }
}
