package eyesatop.util.observablelistidan;

/**
 * Created by Idan on 18/12/2017.
 */

public class ListOperationException extends Exception {
    private final String message;

    public ListOperationException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
