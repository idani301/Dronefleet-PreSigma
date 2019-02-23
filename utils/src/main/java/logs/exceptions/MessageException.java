package logs.exceptions;

public class MessageException extends Exception{
    private final String errorString;

    public MessageException(String errorString) {
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}
