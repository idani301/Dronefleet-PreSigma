package logs.commands;

import logs.JavaLoggerType;
import logs.SingleLogger;

public class WriteErrorCommand extends LoggerCommand {

    private final Throwable e;
    private final String message;

    public WriteErrorCommand(JavaLoggerType javaLoggerType, Throwable e, String message) {
        super(javaLoggerType);
        this.e = e;
        this.message = message;
    }


    @Override
    public void perform(SingleLogger singleLogger) {
        singleLogger.writeError(e,message);
    }
}
