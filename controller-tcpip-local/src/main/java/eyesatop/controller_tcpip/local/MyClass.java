package eyesatop.controller_tcpip.local;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyClass {
    public static void main(String[] args){

        System.out.println(null instanceof Object);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future future = executorService.submit(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i < 1000; i++){
                    System.out.println(i);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

//        future.cancel(true);
        System.out.println("interuppting");
        executorService.shutdownNow();
    }
}
