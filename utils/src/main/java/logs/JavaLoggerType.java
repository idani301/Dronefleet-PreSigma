package logs;

public enum JavaLoggerType {

    ERROR("Error"),
    HARTECH_SERVER("Course Agent Server"),
    EYESATOP_SERVER("Eyesatop Server"),
    ELBIT_RECEIVED("Elbit Received"),
    ELBIT_SENT("Elbit Sent"),
    ELBIT_CONNECTION("Elbit Connections"),
    SWAP("Swap"),
    ANYTARGET("Anytarget"),
    LOOK_AT_POINT("Look at"),
    DEBUG("Debug");

    private final String name;

    JavaLoggerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
