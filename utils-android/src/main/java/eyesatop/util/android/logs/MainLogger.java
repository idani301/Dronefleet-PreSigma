package eyesatop.util.android.logs;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import logs.LoggerTypes;


/**
 * This class responsiable for all logs the pipe will create.
 * Flight debug - will collect all relevant data that will help to understand what exactly
 *                happened during the flight inside the proper timestamp.
 * Program debug - will collect all relevant data related to the progrem run inside the proper
 *                 timestamps.
 */
public class MainLogger {

    public static MainLogger logger = new MainLogger();

    public static final String TAB = "\n" +  "             \t";
    public static final String JUST_TAB = "" +  "             \t";

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");

    private HashMap<LoggerTypes,GeneralLogger> loggers_map                  = new HashMap<>();
    private HashMap<LoggerTypes,BlockingQueue<String>> loggersMessageQueues = new HashMap<>();
    private HashMap<LoggerTypes,WritingLogThread> loggersWritingThreads     = new HashMap<>();

    private final File rootDirectory;

    public static void initInstance(File rootDirectory,String logsName, List<LoggerTypes> logsInclude){

        logger = new MainLogger(rootDirectory, logsName);

        for(LoggerTypes type : logsInclude){
            try {
                if(!logger.is_log_exists(type)) {
                    logger.add_logger(type, type.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static MainLogger getInstance() throws NullPointerException{

        if(logger == null){
            throw new NullPointerException("You need to init the logger");
        }

        return logger;
    }

    private File root_directory;

    private class WritingLogThread extends Thread {

        private LoggerTypes logType;

        public WritingLogThread(LoggerTypes logType){
            this.logType = logType;
        }

        public void run(){

            while(true){
                try {

                    String message = loggersMessageQueues.get(logType).take();
//                    while(loggersMessageQueues.get(logType).size() > 0){
//                        // Spaces like:
//                        //            HH:mm:ss:SSS:
//                        message += "\n             " + loggersMessageQueues .get(logType).take();
//                    }
                    actualWritingIntoLog(logType,message);
//                    Thread.sleep(1000);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private MainLogger(){
        rootDirectory = null;
    }

    public MainLogger(File rootDirectory, String run_area_directory){
        this.rootDirectory = rootDirectory;

        int counter = 0;
        boolean is_created = false;

        while(is_created == false){

            counter++;

            root_directory = new File(rootDirectory, "/Run_" + counter);

            if(!root_directory.exists()){
                root_directory.mkdirs();
                is_created = true;
            }
        }
    }

    public void add_logger(LoggerTypes log_type,String log_file_name) throws IOException {

        loggers_map.put(log_type,new GeneralLogger(root_directory.getAbsolutePath(),log_file_name));

        if(!loggersMessageQueues.containsKey(log_type)){
            loggersMessageQueues.put(log_type,new LinkedBlockingQueue<String>());
            loggersWritingThreads.put(log_type,new WritingLogThread(log_type));
            loggersWritingThreads.get(log_type).start();
        }
    }


    public void close_log(LoggerTypes logType){
        loggers_map.get(logType).close_log();
    }

    public void reopen_log(LoggerTypes logType) throws IOException {
        loggers_map.get(logType).reopen_log();
    }

    public String get_log_path(LoggerTypes log_type){

        if (loggers_map.get(log_type) == null){
            return null;
        }
        else{
            return loggers_map.get(log_type).get_log_path();
        }
    }

    public boolean is_log_exists(LoggerTypes log_type){

        if(loggers_map == null){
            return false;
        }

        if (loggers_map.get(log_type) == null){
            return false;
        }
        else{
            return true;
        }
    }

    public void write_message_now(LoggerTypes logType, String message){

        if(!is_log_exists(logType)){
            return;
        }

        actualWritingIntoLog(logType,message);
    }

    public void write_message(LoggerTypes logType, String message){

        if(!is_log_exists(logType)){
            return;
        }

        loggersMessageQueues.get(logType).add(message);
    }

    public void writeError(LoggerTypes logType, Throwable t) {
        StringWriter messageWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(messageWriter);
        t.printStackTrace(pw);
        pw.flush();
        pw.close();
        write_message_now(logType,"Fatal Error : " + messageWriter.toString());
    }

    private void actualWritingIntoLog(LoggerTypes log_type, String message){

        boolean is_opened = false;

        try{
            reopen_log(log_type);
            is_opened = true;

            Calendar cal = Calendar.getInstance();

            if(loggers_map.get(log_type) == null){

            }
            else{
                String currentTime = sdf.format(cal.getTime());
                loggers_map.get(log_type).write_message(currentTime +  ":" + message);
            }
            close_log(log_type);
        } catch (IOException e) {
            e.printStackTrace();
            if(is_opened) {
                try {
                    close_log(log_type);
                }
                catch(Exception e1){
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
