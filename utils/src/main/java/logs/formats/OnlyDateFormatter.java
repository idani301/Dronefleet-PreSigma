package logs.formats;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class OnlyDateFormatter extends Formatter {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
    private final Date date = new Date();

    @Override
    public String format(LogRecord logRecord) {

        date.setTime(logRecord.getMillis());
        String messageString = simpleDateFormat.format(date) + " - ";
        messageString += logRecord.getMessage() + "\r\n";

        if (logRecord.getThrown() != null) {
            StringWriter var5 = new StringWriter();
            PrintWriter var6 = new PrintWriter(var5);
            logRecord.getThrown().printStackTrace(var6);
            var6.close();
            messageString += var5.toString() + "\r\n";
        }

        return messageString;
    }
}
