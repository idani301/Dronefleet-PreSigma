package eyesatop.unit.ui.models.specialfunctions;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.SpecialFunctionType;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by einav on 29/06/2017.
 */

public class SpecialFunctionMenu extends AbstractViewModel<View> {

    private final HashMap<SpecialFunctionType,SpecialFunctionImageViewModel> buttonsMap = new HashMap<>();
    private final HashMap<SpecialFunctionType,TextViewModel> textMap = new HashMap<>();


    private final View mainView;

    private final ViewModel includeNormal;
    private final ViewModel includeExecution;
    private final ViewModel includePlanner;

    private final TextViewModel normalText;
    private final TextViewModel executionText;
    private final TextViewModel planText;

    private final SpecialFunctionImageViewModel normalButton;
    private final SpecialFunctionImageViewModel executionButton;
    private final SpecialFunctionImageViewModel planButton;


    public void setAlwaysGone(SpecialFunctionType type,boolean isAlwaysGone){
        switch (type){

            case NORMAL:
                includeNormal.setAlwaysGone(isAlwaysGone);
                break;
            case MISSION_EXECUTION:
                includeExecution.setAlwaysGone(isAlwaysGone);
                break;
            case MISSION_PLAN:
                includePlanner.setAlwaysGone(isAlwaysGone);
                break;
        }
    }

    public SpecialFunctionMenu(View view) {

        super(view);

        includeNormal = new ViewModel(view.findViewById(R.id.specialFunctionsIncludeNormal));
        includeExecution = new ViewModel(view.findViewById(R.id.specialFunctionsIncludeMissionExecution));
        includePlanner = new ViewModel(view.findViewById(R.id.specialFunctionsIncludeMissionPlan));

        normalButton = new SpecialFunctionImageViewModel(super.<ImageView>find(R.id.mainMenuNormalFunctionButton));
        normalText = new TextViewModel((TextView) super.find(R.id.mainMenuNormalFunctionText));
        textMap.put(SpecialFunctionType.NORMAL,normalText);
        buttonsMap.put(SpecialFunctionType.NORMAL,normalButton);

        executionButton = new SpecialFunctionImageViewModel(super.<ImageView>find(R.id.mainMenuExecutionFunctionButton));
        executionText = new TextViewModel((TextView) super.find(R.id.mainMenuExecutionFunctionText));
        textMap.put(SpecialFunctionType.MISSION_EXECUTION,executionText);
        buttonsMap.put(SpecialFunctionType.MISSION_EXECUTION,executionButton);

        planButton = new SpecialFunctionImageViewModel(super.<ImageView>find(R.id.mainMenuPlannerFunctionButton));
        planText = new TextViewModel((TextView) super.find(R.id.mainMenuMissionPlanFunctionText));
        textMap.put(SpecialFunctionType.MISSION_PLAN,planText);
        buttonsMap.put(SpecialFunctionType.MISSION_PLAN,planButton);

        mainView = view;
    }

    public Removable bindToNull(){
        mainView.setVisibility(View.GONE);
        return Removable.STUB;
    }

    public Removable bindToTab(final DroneTabModel tabModel){

        final ArrayList<Removable> removablesList = new ArrayList<>();

        for(final SpecialFunctionType type : SpecialFunctionType.values()){

            if(buttonsMap.containsKey(type)) {

                buttonsMap.get(type).singleTap().set(new Function<MotionEvent, Boolean>() {
                    @Override
                    public Boolean apply(MotionEvent input) {

                        tabModel.getFunctionsModel().setCurrentFunction(type);
                        tabModel.getFunctionsModel().isFunctionScreenOpen().set(false);
                        return false;
                    }
                });
            }
        }

        removablesList.add(tabModel.getFunctionsModel().getCurrentFunction().observe(new Observer<SpecialFunction>() {
            @Override
            public void observe(SpecialFunction oldValue, SpecialFunction newValue, Observation<SpecialFunction> observation) {

                for(SpecialFunctionType type : buttonsMap.keySet()){
                    if(type == newValue.functionType()){
                        buttonsMap.get(type).selected().setIfNew(true);
                        textMap.get(type).textColor().set(Color.CYAN);
                    }
                    else{
                        buttonsMap.get(type).selected().setIfNew(false);
                        textMap.get(type).textColor().set(Color.WHITE);
                    }
                }
            }
        }).observeCurrentValue());

        removablesList.add(tabModel.getFunctionsModel().isFunctionScreenOpen().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                mainView.setVisibility(newValue ? View.VISIBLE : View.GONE);
            }
        },UI_EXECUTOR).observeCurrentValue());

        return new RemovableCollection(removablesList);
    }
}
