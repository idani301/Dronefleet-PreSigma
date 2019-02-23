package eyesatop.controller.mission.exceptions;

/**
 * Created by Idan on 03/09/2017.
 */

public class IteratorException extends Exception {
    private final String string;

    public IteratorException(String string) {
        this.string = string;
    }
}
