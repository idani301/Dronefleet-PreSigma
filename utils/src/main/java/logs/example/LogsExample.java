package logs.example;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import logs.JavaLoggerType;
import logs.MainLoggerJava;

public class LogsExample {
    public static void main(String[] args){

        try {

            MainLoggerJava.writeMessage(JavaLoggerType.DEBUG,"Illegal message");

            MainLoggerJava.init(new File("D:/My Logs"), Arrays.asList(JavaLoggerType.ERROR, JavaLoggerType.DEBUG));
        } catch (IOException e) {
//            e.printStackTrace();
        }

        MainLoggerJava.writeMessage(JavaLoggerType.DEBUG,"Hi 1");
        MainLoggerJava.writeMessage(JavaLoggerType.DEBUG,"Hi 2");
        MainLoggerJava.writeMessage(JavaLoggerType.DEBUG,"Hi 3");

        MainLoggerJava.writeMessage(JavaLoggerType.ERROR,"Hi 1");
        MainLoggerJava.writeMessage(JavaLoggerType.ERROR,"Hi 2");
        MainLoggerJava.writeMessage(JavaLoggerType.ERROR,"Hi 3");

        try{
            throw new IllegalArgumentException("Illegal Argumant crap");
        }
        catch (Exception e){
            MainLoggerJava.writeError(JavaLoggerType.ERROR,"Hi",e);
        }
    }
}
