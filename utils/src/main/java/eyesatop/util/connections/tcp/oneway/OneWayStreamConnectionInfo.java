package eyesatop.util.connections.tcp.oneway;

import java.util.Objects;

public class OneWayStreamConnectionInfo<T> {
    private final String ip;
    private final int port;
    private final OneWayStreamCallback<T> callback;

    public OneWayStreamConnectionInfo(String ip, int port, OneWayStreamCallback<T> callback) {
        this.ip = ip;
        this.port = port;
        this.callback = callback;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public OneWayStreamCallback<T> getCallback() {
        return callback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OneWayStreamConnectionInfo)) return false;
        OneWayStreamConnectionInfo<?> that = (OneWayStreamConnectionInfo<?>) o;
        return getPort() == that.getPort() &&
                Objects.equals(getIp(), that.getIp()) &&
                Objects.equals(getCallback(), that.getCallback());
    }
}
