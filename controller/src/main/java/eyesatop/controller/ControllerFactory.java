package eyesatop.controller;

import eyesatop.util.future.Future;

public interface ControllerFactory {
    Future<? extends DroneController> newController() throws Exception;
}
