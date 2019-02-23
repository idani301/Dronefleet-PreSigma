package eyesatop.util.connections;

import java.util.concurrent.TimeUnit;

public class TimeoutInfo {
    private final long timeout;
    private final TimeUnit timeUnit;

    public TimeoutInfo(long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
