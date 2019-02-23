package com.example.abstractcontroller.tasks.camera;

import com.example.abstractcontroller.AbstractDroneController;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.FormatSDCard;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 18/12/2017.
 */

public class FormatSDCardAbstract extends RunnableDroneTask<CameraTaskType> implements FormatSDCard {

    private final AbstractDroneController controller;

    public FormatSDCardAbstract(AbstractDroneController controller) {
        this.controller = controller;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.FORMAT_SD_CARD;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        controller.camera().internalFormatSDCard();
    }
}
