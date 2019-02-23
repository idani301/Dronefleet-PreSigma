package eyesatop.unit.ui.models.tabs;

import android.app.Activity;

import eyesatop.controller.DroneController;
import eyesatop.controller.mock.MockController;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;

/**
 * Created by Idan on 01/01/2018.
 */

public interface DroneTab {
    UiDroneTasks getDroneTasks();
    FunctionsModel getFunctionsModel();
    DroneController getDroneController();

    public static class Stub implements DroneTab {

        private final Activity activity;
        private final MockController controller = new MockController.Stub();
        private final UiDroneTasks uiDroneTasks;
        private final FunctionsModel functionsModel;

        public Stub(Activity activity, MapViewModel mapViewModel, MessageViewModel messageViewModel, LittleMessageViewModel littleMessageViewModel) {
            this.activity = activity;

            uiDroneTasks = new UiDroneTasks(controller,null);
            functionsModel = new FunctionsModel(mapViewModel,controller,messageViewModel,littleMessageViewModel, activity);
        }

        @Override
        public UiDroneTasks getDroneTasks() {
            return uiDroneTasks;
        }

        @Override
        public FunctionsModel getFunctionsModel() {
            return functionsModel;
        }

        @Override
        public DroneController getDroneController() {
            return controller;
        }
    }
}
