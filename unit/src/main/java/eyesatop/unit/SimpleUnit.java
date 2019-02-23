package eyesatop.unit;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.unit.exceptions.ComponentNotFoundException;
import eyesatop.unit.tasks.UnitTaskType;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

public class SimpleUnit implements DroneUnit {

    private final BooleanProperty isRCConnected = new BooleanProperty(false);

    private final ObservableList<DroneController> controllers = new ObservableList<>();
    private final SimpleFlightTasks flightTasks;
    private final Lock controllersLock = new ReentrantLock();
    private final Property<DroneController> selectedDrone = new Property<>();
    private final DtmProvider dtmProvider;
    private final HashMap<UUID,Property<DroneTask<UnitTaskType>>> taskHashMap = new HashMap<>();

    public SimpleUnit(DtmProvider dtmProvider) {
        this.dtmProvider = dtmProvider;
        flightTasks = new SimpleFlightTasks(controllers,dtmProvider, taskHashMap);
    }

    @Override
    public BooleanProperty isRCConnected() {
        return isRCConnected;
    }

    @Override
    public ObservableList<DroneController> controllers() {
        return controllers;
    }

    @Override
    public void addController(DroneController controller) {
        controllersLock.lock();
        try{
            if(!controllers.contains(controller)){
                controllers.add(controller);
                taskHashMap.put(controller.uuid(),new Property<DroneTask<UnitTaskType>>());
            }
        }
        finally {
            controllersLock.unlock();
        }
    }

    @Override
    public void removeController(DroneController controller) {
        controllersLock.lock();
        try{
            controllers.remove(controller);
            taskHashMap.remove(controller.uuid());
        }
        finally {
            controllersLock.unlock();
        }
    }

    @Override
    public void setSelectedDrone(DroneController controller) {
        selectedDrone.set(controller);
    }

    @Override
    public ObservableValue<DroneTask<UnitTaskType>> currentTask(UUID uuid) {
        return taskHashMap.get(uuid);
    }

    public ObservableValue<DroneController> getSelectedDrone() {
        return selectedDrone;
    }

    @Override
    public DroneController getControllerByUUID(UUID uuid) throws ComponentNotFoundException {

        controllersLock.lock();
        try{
            for(DroneController controller : controllers){
                try {
                    if (uuid.equals(controller.uuid())) {
                        return controller;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            throw new ComponentNotFoundException();
        }
        finally {
            controllersLock.unlock();
        }
    }

    public HashMap<UUID, Property<DroneTask<UnitTaskType>>> getTaskHashMap() {
        return taskHashMap;
    }

    @Override
    public UnitFlightTasks flightTasks() {
        return flightTasks;
    }
}
