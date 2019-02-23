package eyesatop.controller.tasks;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.Cancellable;
import eyesatop.util.Predicate;
import eyesatop.util.Removable;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public abstract class RunnableDroneTask<S extends EnumWithName> implements DroneTask<S>, Runnable {
    private final UUID uuid;
    private Cancellable cancellable;
    private final Lock lock;
    private final Property<DroneTaskException> error;
    private final Property<TaskStatus> status;
    private final Property<TaskProgressState> state;
    protected RunnableDroneTask() {
        uuid = UUID.randomUUID();
        lock = new ReentrantLock();
        error = new Property<>(null);
        status = new Property<>(TaskStatus.NOT_STARTED);
        state = new Property<>();
        cancellable = new Cancellable() {
            @Override
            public void cancel() {
                lock.lock();
                try {
                    if (status.is(TaskStatus.RUNNING) || status.is(TaskStatus.NOT_STARTED)) {
                        status.set(TaskStatus.CANCELLED);
                    } else if (status.is(TaskStatus.CANCELLED)) {
                        throw new IllegalStateException("task was already cancelled");
                    }
                } finally {
                    lock.unlock();
                }
            }
        };
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public ObservableValue<DroneTaskException> error() {
        return error;
    }

    @Override
    public ObservableValue<TaskStatus> status() {
        return status;
    }

    @Override
    public ObservableValue<TaskProgressState> progressState() {
        return state;
    }

    @Override
    public final void cancel() {
        lock.lock();
        try {
            if (cancellable != null) {
                cancellable.cancel();
            }
        }catch (IllegalStateException e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        lock.lock();
        try {
            if (!TaskStatus.NOT_STARTED.equals(status.value())) {
                throw new IllegalStateException("task has already run.");
            }
            final Thread thread = Thread.currentThread();
            cancellable = new Cancellable() {
                @Override
                public void cancel() {
                    thread.interrupt();
                }
            };
            status.set(TaskStatus.RUNNING);
        } finally {
            lock.unlock();
        }

        try {
            perform(state);
        } catch (DroneTaskException e) {
            error.set(e);
        } catch (InterruptedException e) {
            error.set(new DroneTaskException("cancelled"));
        }

        lock.lock();
        try {
            cancellable = null;
            status.set(
                    (error.isNull() ? TaskStatus.FINISHED :
                            (error.value().getErrorString().equals("cancelled") ? TaskStatus.CANCELLED
                                    : TaskStatus.ERROR)));
        } finally {
            try {
                cleanUp(status.value());
            } catch(Throwable t){
                t.printStackTrace();
            }
            lock.unlock();
        }
    }

    private Property<ConfirmationData> pendingConfirmation = new Property<>();

    @Override
    public ObservableValue<ConfirmationData> pendingConfirmation() {
        return pendingConfirmation;
    }

    protected void waitForConfirmation(ConfirmationData confirmationData) throws InterruptedException {

        pendingConfirmation.set(confirmationData);
        pendingConfirmation.equalsTo(null).awaitTrue();
    }

    public void confirm(ConfirmationsType confirmationsType) throws DroneTaskException {

        ConfirmationData currentPendingConfirmationData = pendingConfirmation.value();

        if(currentPendingConfirmationData == null || currentPendingConfirmationData.getType() != confirmationsType){
            throw new DroneTaskException("confirmation does not fit the actual pending confirmation");
        }
        pendingConfirmation.set(null);
    }

    protected abstract void perform(Property<TaskProgressState> state) throws DroneTaskException,InterruptedException;
    protected void cleanUp(TaskStatus exitStatus) throws Exception {}
}
