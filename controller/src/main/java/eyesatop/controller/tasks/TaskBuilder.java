package eyesatop.controller.tasks;

public interface TaskBuilder<T extends DroneTask> {
    T build();
}
