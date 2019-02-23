package eyesatop.util.connections.tcp.oneway;

import java.util.concurrent.BlockingQueue;

public interface OneWayStreamCallback<T> {
    void onConnectionActive(BlockingQueue<T> messagesToSend);
    void onConnectionLost(BlockingQueue<T> messagesToSend);
}
