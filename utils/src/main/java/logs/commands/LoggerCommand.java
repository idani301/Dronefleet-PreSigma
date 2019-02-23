package logs.commands;

import logs.JavaLoggerType;
import logs.SingleLogger;

public abstract class LoggerCommand {

    private final JavaLoggerType javaLoggerType;

    protected LoggerCommand(JavaLoggerType javaLoggerType) {
        this.javaLoggerType = javaLoggerType;
    }

    public JavaLoggerType getJavaLoggerType() {
        return javaLoggerType;
    }

    public abstract void perform(SingleLogger singleLogger);
}
