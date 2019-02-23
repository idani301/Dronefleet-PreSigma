package logs.commands;

import logs.JavaLoggerType;
import logs.SingleLogger;

public class WriteMessageCommand extends LoggerCommand {

    private final String message;

    public WriteMessageCommand(JavaLoggerType javaLoggerType, String message) {
        super(javaLoggerType);
        this.message = message;
    }

    @Override
    public void perform(SingleLogger singleLogger) {
        singleLogger.write(message);
    }
}
