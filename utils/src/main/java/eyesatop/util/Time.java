package eyesatop.util;

import java.util.concurrent.TimeUnit;

public class Time {
    private final long time;
    private final TimeUnit timeUnit;

    public Time(long time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public long get(TimeUnit timeUnit) {
        return timeUnit.convert(time, this.timeUnit);
    }

    public Time delta(Time other) {
        long myTimeNs = get(TimeUnit.NANOSECONDS);
        long otherTimeNs = other.get(TimeUnit.NANOSECONDS);
        return new Time(myTimeNs-otherTimeNs, TimeUnit.NANOSECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Time time1 = (Time) o;

        if (time != time1.time) return false;
        return timeUnit == time1.timeUnit;

    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + (timeUnit != null ? timeUnit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Time{" +
                "time=" + time +
                ", timeUnit=" + timeUnit +
                '}';
    }
}
