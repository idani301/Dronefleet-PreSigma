package eyesatop.controller.tasks.stabs;

import java.util.UUID;

import eyesatop.controller.tasks.ConfirmationData;
import eyesatop.controller.tasks.ConfirmationsType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by einav on 25/07/2017.
 */

public abstract class StubDroneTask<S extends EnumWithName> implements DroneTask<S> {

    private DroneTask<S> droneTask;

    private RemovableCollection bindings  = new RemovableCollection();

    private final Property<DroneTaskException> error = new Property<>();
    private final Property<TaskStatus> status = new Property<>();
    private final Property<TaskProgressState> state = new Property<>();
    private Property<ConfirmationData> pendingConfirmation = new Property<>();

    @Override
    public void cancel() {
        DroneTask currentTask = droneTask;

        try {
            if (currentTask != null) {
                currentTask.cancel();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public UUID uuid() {

        DroneTask currentTask = droneTask;

        try{
            return currentTask == null ? null : currentTask.uuid();
        }
        catch (Exception e){
            return null;
        }
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
    public ObservableValue<ConfirmationData> pendingConfirmation() {
        return pendingConfirmation;
    }

    @Override
    public void confirm(ConfirmationsType confirmationsType) throws DroneTaskException {

        DroneTask currentTask = droneTask;

        try {
            if (currentTask != null) {
                currentTask.confirm(confirmationsType);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    protected RemovableCollection getBindings() {
        return bindings;
    }

    public void bindToTask(DroneTask<S> task){

        bindings.remove();
        this.droneTask = task;

        bindings.add(this.error.bind(task.error()));
        bindings.add(this.status.bind(task.status()));
        bindings.add(this.error.bind(task.error()));
        bindings.add(this.pendingConfirmation.bind(task.pendingConfirmation()));
        bindings.add(task.status().observe(new Observer<TaskStatus>() {
            @Override
            public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                if(newValue.isTaskDone()){
                    bindings.remove();
                }
            }
        }).observeCurrentValue());
    }
}
