package eyesatop.unit.ui.models.tabs;

import android.app.Activity;

import eyesatop.controller.DroneController;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.specialfunctions.MissionExecutionFunction;
import eyesatop.unit.ui.models.specialfunctions.MissionPlannerFunction;
import eyesatop.unit.ui.models.specialfunctions.NormalFunction;
import eyesatop.unit.ui.models.specialfunctions.SpecialFunction;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Property;

/**
 * Created by einav on 27/06/2017.
 */
public class FunctionsModel {

    private final Property<SpecialFunction> currentFunction = new Property<>();
    private BooleanProperty isFunctionScreenOpen = new BooleanProperty(false);

    private final NormalFunction normalFunction;
//    private final ObliFunction obliFunction;
    private final MissionExecutionFunction missionExecutionFunction;
    private final MissionPlannerFunction missionPlannerFunction;

    private final LittleMessageViewModel littleMessageViewModel;

    private final Activity activity;

    public FunctionsModel(MapViewModel mapViewModel, DroneController controller, MessageViewModel messageViewModel, LittleMessageViewModel littleMessageViewModel, Activity activity) {
        this.littleMessageViewModel = littleMessageViewModel;

        normalFunction = new NormalFunction(activity,isFunctionScreenOpen);
        currentFunction.set(normalFunction);

        missionExecutionFunction = new MissionExecutionFunction(activity,controller,mapViewModel,isFunctionScreenOpen,messageViewModel,littleMessageViewModel);
//        obliFunction = new ObliFunction(activity,isFunctionScreenOpen);
        missionPlannerFunction = new MissionPlannerFunction(activity,isFunctionScreenOpen);

        this.activity = activity;
    }

    public BooleanProperty isFunctionScreenOpen() {
        return isFunctionScreenOpen;
    }

    public void setCurrentFunction(SpecialFunctionType newFunctionType){

        switch (newFunctionType){

            case NORMAL:
                currentFunction.set(normalFunction);
                break;
            case MISSION_EXECUTION:
                currentFunction.set(missionExecutionFunction);
                missionExecutionFunction.actionMenuButtonPressed();
                break;
            case MISSION_PLAN:
                currentFunction.set(missionPlannerFunction);
                missionPlannerFunction.actionMenuButtonPressed();
                break;
        }
    }

//    public ObliFunction getObliFunction() {
//        return obliFunction;
//    }


    public MissionExecutionFunction getMissionExecutionFunction() {
        return missionExecutionFunction;
    }

    public MissionPlannerFunction getMissionPlannerFunction() {
        return missionPlannerFunction;
    }

    public Property<SpecialFunction> getCurrentFunction() {
        return currentFunction;
    }
}
