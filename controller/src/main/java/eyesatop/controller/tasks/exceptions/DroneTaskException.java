package eyesatop.controller.tasks.exceptions;

/**
 * Created by einav on 27/01/2017.
 */

public class DroneTaskException extends Exception {

    private final String errorString;

    public DroneTaskException(Throwable cause) {
        super(cause);
        this.errorString = cause.getMessage();
    }

    public DroneTaskException(String errorString) {
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DroneTaskException that = (DroneTaskException) o;

        return errorString != null ? errorString.equals(that.errorString) : that.errorString == null;

    }

    @Override
    public int hashCode() {
        return errorString != null ? errorString.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DroneTaskException{" +
                "errorString='" + errorString + '\'' +
                '}';
    }
}
