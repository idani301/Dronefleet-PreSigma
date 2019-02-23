package eyesatop.util.connections.tcp.requestresponse;

import java.util.concurrent.CountDownLatch;

import eyesatop.util.model.Property;

public class RequestResponseMessageInfo<T,S> {
    private final T request;
    private final Property<S> response = new Property<>();
    private final Property<String> error = new Property<>();
    private final CountDownLatch latch = new CountDownLatch(1);

    public RequestResponseMessageInfo(T request) {
        this.request = request;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public T getRequest() {
        return request;
    }

    public Property<S> getResponse() {
        return response;
    }

    public Property<String> getError() {
        return error;
    }
}
