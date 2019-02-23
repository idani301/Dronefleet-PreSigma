package logs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import logs.commands.LoggerCommand;
import logs.commands.WriteErrorCommand;
import logs.commands.WriteMessageCommand;


public class MultiLogger {

    private final HashMap<JavaLoggerType,SingleLogger> singleLoggersMap = new HashMap<>();
    private ExecutorService loggerExecutor;
    private final BlockingQueue<LoggerCommand> commands = new LinkedBlockingQueue<>();

    public MultiLogger(File rootDirectory, List<JavaLoggerType> javaLoggerTypeList) throws IOException {

        loggerExecutor = Executors.newSingleThreadExecutor();
        loggerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        commands.take();
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    }
                }
            }
        });

        if(javaLoggerTypeList == null || rootDirectory == null){
            return;
        }

        if(!rootDirectory.exists()){
            if(!rootDirectory.mkdirs()){
                throw new IOException("Unable to create root directory : " + rootDirectory.getAbsolutePath());
            }
        }

        for(JavaLoggerType javaLoggerType : javaLoggerTypeList){
            try {
                SingleLogger singleLogger = new SingleLogger(rootDirectory, javaLoggerType);
                singleLoggersMap.put(javaLoggerType,singleLogger);
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }

        start();
    }

    private void start() {

        loggerExecutor.shutdownNow();
        loggerExecutor = Executors.newSingleThreadExecutor();

        loggerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        LoggerCommand command = commands.take();
                        SingleLogger singleLogger = singleLoggersMap.get(command.getJavaLoggerType());
                        if(singleLogger != null){
                            command.perform(singleLogger);
                        }
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
    }

    public void writeMessage(JavaLoggerType type, String message){
        commands.add(new WriteMessageCommand(type,message));
    }

    public void writeError(JavaLoggerType type, String message, Throwable e){
        commands.add(new WriteErrorCommand(type,e,message));
    }

    public void destroy(){
        if(loggerExecutor != null) {
            loggerExecutor.shutdownNow();
        }
    }
}
