package logs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainLoggerJava {

    private static MultiLogger instance = null;

    private static final Lock instanceLock = new ReentrantLock();

    static {
        try {
            instance = new MultiLogger(null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(File rootDirectory, List<JavaLoggerType> javaLoggerTypeList) throws IOException {

        instanceLock.lock();

        try {

            if (instance != null) {
                instance.destroy();
            }

            int counter = 0;
            boolean is_created = false;
            File fileTry = null;

            while (is_created == false) {

                counter++;

                fileTry = new File(rootDirectory, "/Run_" + counter);

                if (!fileTry.exists()) {
                    if (!fileTry.mkdirs()) {
                        throw new IOException("Unable to create : " + fileTry.getAbsolutePath());
                    }
                    is_created = true;
                }
            }

            instance = new MultiLogger(fileTry, javaLoggerTypeList);
        }
        finally {
            instanceLock.unlock();
        }
    }

    public static void writeMessage(JavaLoggerType type, String message){
        instance.writeMessage(type, message);
    }

    public static void writeError(JavaLoggerType type, String message, Throwable e){
        instance.writeError(type, message, e);
    }
}
