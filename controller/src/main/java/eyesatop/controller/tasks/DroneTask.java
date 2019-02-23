package eyesatop.controller.tasks;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.util.Cancellable;
import eyesatop.util.Removable;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public interface DroneTask<T extends EnumWithName> extends Cancellable {

    UUID uuid();

    ObservableValue<DroneTaskException> error();
    ObservableValue<TaskStatus> status();
    ObservableValue<TaskProgressState> progressState();
    ObservableValue<ConfirmationData> pendingConfirmation();
    void confirm(ConfirmationsType confirmationsType) throws DroneTaskException;

    T taskType();
}
