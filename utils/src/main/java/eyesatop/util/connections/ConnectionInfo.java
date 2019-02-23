package eyesatop.util.connections;

public class ConnectionInfo {
    private final String ip;
    private final int port;

    public ConnectionInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionInfo)) return false;
        ConnectionInfo that = (ConnectionInfo) o;
        return getPort() == that.getPort() &&
                getIp().equals(that.getIp());
    }
}
