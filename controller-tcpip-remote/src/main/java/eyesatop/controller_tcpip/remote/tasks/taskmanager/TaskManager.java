package eyesatop.controller_tcpip.remote.tasks.taskmanager;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasks.CancelTaskRequest;
import eyesatop.controller_tcpip.common.tasks.TaskUpdate;
import eyesatop.controller_tcpip.remote.tcpipcontroller.TCPController;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class TaskManager<T extends EnumWithName> {

    private final HashMap<UUID,MockDroneTask<T>> tasksMap = new HashMap<>();
    private final Lock mapLock = new ReentrantLock();

    private final TCPController controller;

    public TaskManager(TCPController controller) {
        this.controller = controller;
    }

    public void clearMap(){
        mapLock.lock();
        try{
            for(MockDroneTask<T> task : tasksMap.values()){
                task.error().set(new DroneTaskException("Clearing All Tasks"));
                task.status().set(TaskStatus.ERROR);
            }
        }
        finally {
            mapLock.unlock();
        }
    }

    public MockDroneTask<T> updateTask(final TaskUpdate<T> taskUpdate){
        mapLock.lock();
        try{
            MockDroneTask<T> taskFromMap = tasksMap.get(taskUpdate.getUuid());
            if(taskFromMap != null){
                taskUpdate.updateMockTask(taskFromMap);
                return taskFromMap;
            }
            else{
                final MockDroneTask<T> newTask = taskUpdate.createMockTask();
                newTask.setCancelRunnable(new Runnable() {
                    @Override
                    public void run() {
                        controller.getCancelTasksClient().addMessage(new CancelTaskRequest(newTask.uuid()));
                    }
                });

                tasksMap.put(taskUpdate.getUuid(),newTask);
                newTask.status().observe(new Observer<TaskStatus>() {
                    @Override
                    public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                        if(newValue.isTaskDone()){
                            tasksMap.remove(taskUpdate.getUuid());
                        }
                    }
                }).observeCurrentValue();
                return newTask;
            }
        }
        finally {
            mapLock.unlock();
        }
    }

    public MockDroneTask<T> getTask(TaskUpdate<T> taskUpdate){
        mapLock.lock();
        try{
            MockDroneTask taskFromMap = tasksMap.get(taskUpdate.getUuid());
            if(taskFromMap != null){
                return taskFromMap;
            }
            else{
                final MockDroneTask<T> newTask = taskUpdate.createMockTask();
                newTask.setCancelRunnable(new Runnable() {
                    @Override
                    public void run() {
                        controller.getCancelTasksClient().addMessage(new CancelTaskRequest(newTask.uuid()));
                    }
                });
                tasksMap.put(newTask.uuid(),newTask);
                newTask.status().observe(new Observer<TaskStatus>() {
                    @Override
                    public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                        if(newValue.isTaskDone()){
                            tasksMap.remove(newTask.uuid());
                        }
                    }
                }).observeCurrentValue();
                return newTask;
            }
        }
        finally {
            mapLock.unlock();
        }
    }
}
