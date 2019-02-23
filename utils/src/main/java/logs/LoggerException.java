package logs;


import logs.exceptions.MessageException;

public class LoggerException extends MessageException {

    public LoggerException(String errorString) {
        super(errorString);
    }
}
