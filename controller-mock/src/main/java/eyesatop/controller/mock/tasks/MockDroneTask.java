package eyesatop.controller.mock.tasks;

import java.util.Objects;
import java.util.UUID;

import eyesatop.controller.tasks.ConfirmationData;
import eyesatop.controller.tasks.ConfirmationsType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.RunnableStub;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

public abstract class MockDroneTask<T extends EnumWithName> implements DroneTask<T> {

    private final UUID uuid;
    private final T taskType;

    private final Property<DroneTaskException> error;
    private final Property<TaskStatus> status;
    private final Property<TaskProgressState> progressState;
    private final Property<ConfirmationData> pendingConfirmation;

    private transient Runnable cancelRunnable = RunnableStub.INSTANCE;

    public MockDroneTask(UUID uuid, T taskType) {
        this.uuid = uuid;
        this.taskType = taskType;

        error = new Property<>();
        status = new Property<>();
        progressState = new Property<>();
        pendingConfirmation = new Property<>();
    }

    @Override
    public void confirm(ConfirmationsType confirmationsType) throws DroneTaskException {
    }

    @Override
    public ObservableValue<ConfirmationData> pendingConfirmation() {
        return pendingConfirmation;
    }

    @Override public UUID uuid() {return uuid;}
    @Override public T taskType() {return taskType;}

    @Override public Property<DroneTaskException> error() {return error;}
    @Override public Property<TaskStatus> status() {return status;}
    @Override public Property<TaskProgressState> progressState() {return progressState;}

    public void setCancelRunnable(Runnable cancelRunnable) {
        this.cancelRunnable = cancelRunnable;
    }

    @Override public void cancel() {
        cancelRunnable.run();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MockDroneTask)) return false;
        MockDroneTask<?> task = (MockDroneTask<?>) o;
        return ((MockDroneTask) o).uuid.equals(task.uuid);
    }

}
