package eyesatop.util.connections.tcp.requestresponse;

public class RequestResponseException extends Exception {

    private final String errorMessage;

    public RequestResponseException(String messageError) {
        this.errorMessage = messageError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
