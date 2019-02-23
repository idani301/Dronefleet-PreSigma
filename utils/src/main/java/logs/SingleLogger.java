package logs;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import logs.formats.OnlyDateFormatter;


public class SingleLogger {

    private static final SimpleFormatter formatter = new SimpleFormatter();
    private static final OnlyDateFormatter onlyDateFormatter = new OnlyDateFormatter();

    private final File rootDirectory;
    private final JavaLoggerType javaLoggerType;
    private final Logger javaLogger;

    public SingleLogger(File rootDirectory, JavaLoggerType javaLoggerType) throws IOException {

        this.rootDirectory = rootDirectory;
        this.javaLoggerType = javaLoggerType;

        File logFile = new File(rootDirectory, javaLoggerType.getName() + ".txt");

        if(logFile.exists()){
            if(!logFile.delete()){
                throw new IOException("Unable to delete current log file : " + logFile.getAbsolutePath());
            }
        }

        if(!rootDirectory.exists()){
            if(!rootDirectory.mkdirs()){
                throw new IOException("Unable to Create the folder : " + rootDirectory.getAbsolutePath());
            }
        }

        if(!logFile.createNewFile()){
            throw new IOException("Unable to create the log file : " + logFile.getAbsolutePath());
        }

        javaLogger = Logger.getLogger(javaLoggerType.getName());
        FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath());
        javaLogger.addHandler(fileHandler);
        fileHandler.setFormatter(formatter);
        javaLogger.info("File Created");
        fileHandler.setFormatter(onlyDateFormatter);
    }

    public void write(String message){
        javaLogger.info(message);
    }

    public void writeError(Throwable e,String message){
        javaLogger.log(Level.WARNING,message,e);
    }
}
