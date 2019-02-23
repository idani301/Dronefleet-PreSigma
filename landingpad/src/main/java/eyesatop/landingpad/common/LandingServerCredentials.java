//package eyesatop.landingpad.common;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import java.util.UUID;
//
//import eyesatop.util.geo.Location;
//
//public class LandingServerCredentials {
//
//    private static final String UUID = "uuid";
//    private static final String HOST = "host";
//    private static final String PORT = "port";
//    private static final String LOCATION = "location";
//    private static final String ANGLE = "angle";
//
//    private final UUID uuid;
//    private final String host;
//    private final int port;
//    private final Location location;
//    private final Double angle;
//
//    @JsonCreator
//    public LandingServerCredentials(
//            @JsonProperty(UUID)
//            UUID uuid,
//
//            @JsonProperty(HOST)
//            String host,
//
//            @JsonProperty(PORT)
//            int port,
//
//            @JsonProperty(LOCATION)
//            Location location,
//
//            @JsonProperty(ANGLE)
//            Double angle) {
//        this.uuid = uuid;
//        this.host = host;
//        this.port = port;
//        this.location = location;
//        this.angle = angle;
//    }
//
//    @JsonProperty(UUID)
//    public UUID uuid() {
//        return uuid;
//    }
//
//    public LandingServerCredentials uuid(UUID uuid) {
//        return new LandingServerCredentials(uuid, host, port, location, angle);
//    }
//
//    @JsonProperty(HOST)
//    public String host() {
//        return host;
//    }
//
//    public LandingServerCredentials host(String host) {
//        return new LandingServerCredentials(uuid, host, port, location, angle);
//    }
//
//    @JsonProperty(PORT)
//    public int port() {
//        return port;
//    }
//
//    public LandingServerCredentials port(int port) {
//        return new LandingServerCredentials(uuid, host, port, location, angle);
//    }
//
//    @JsonProperty(LOCATION)
//    public Location location() {
//        return location;
//    }
//
//    public LandingServerCredentials location(Location location) {
//        return new LandingServerCredentials(uuid, host, port, location, angle);
//    }
//
//    @JsonProperty(ANGLE)
//    public Double angle() {
//        return angle;
//    }
//
//    public LandingServerCredentials angle(Double angle) {
//        return new LandingServerCredentials(uuid, host, port, location, angle);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        LandingServerCredentials that = (LandingServerCredentials) o;
//
//        if (port != that.port) return false;
//        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
//        if (host != null ? !host.equals(that.host) : that.host != null) return false;
//        if (location != null ? !location.equals(that.location) : that.location != null)
//            return false;
//        return angle != null ? angle.equals(that.angle) : that.angle == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = uuid != null ? uuid.hashCode() : 0;
//        result = 31 * result + (host != null ? host.hashCode() : 0);
//        result = 31 * result + port;
//        result = 31 * result + (location != null ? location.hashCode() : 0);
//        result = 31 * result + (angle != null ? angle.hashCode() : 0);
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        return "LandingServerCredentials{" +
//                "uuid=" + uuid +
//                ", host='" + host + '\'' +
//                ", port=" + port +
//                ", location=" + location +
//                ", angle=" + angle +
//                '}';
//    }
//}
