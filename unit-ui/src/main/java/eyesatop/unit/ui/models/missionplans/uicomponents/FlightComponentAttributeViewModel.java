package eyesatop.unit.ui.models.missionplans.uicomponents;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.map.mission.FlightPlanComponent;
import eyesatop.util.Function;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 15/04/2018.
 */

public class FlightComponentAttributeViewModel extends AbstractViewModel<View> {

    public interface FlightComponentListener {
        public void onTextPressed(FlightPlanComponent flightPlanComponent);
        public void onUpButtonPressed(FlightPlanComponent flightPlanComponent);
        public void onDownButtonPressed(FlightPlanComponent flightPlanComponent);
        public void onDeleteButtonPressed(FlightPlanComponent flightPlanComponent);
        public void onDuplicateButtonPressed(FlightPlanComponent flightPlanComponent);
    }

    private final Property<FlightComponentListener> currentListener = new Property<>();
    private final TextViewModel attributeName;
    private final RemovableCollection bindings = new RemovableCollection();
    private final FlightPlanComponent component;
    private final ImageViewModel upButton;
    private final ImageViewModel downButton;
    private final ImageViewModel deleteButton;
    private final ImageViewModel duplicateButton;

    public FlightComponentAttributeViewModel(View view, final FlightPlanComponent flightPlanComponent) {
        super(view);
        component = flightPlanComponent;
        attributeName = new TextViewModel((TextView) view.findViewById(R.id.attributeName));
        upButton = new ImageViewModel((ImageView)view.findViewById(R.id.ivMoveUp));
        downButton = new ImageViewModel((ImageView)view.findViewById(R.id.ivMoveDown));
        deleteButton = new ImageViewModel((ImageView)view.findViewById(R.id.delete));
        duplicateButton= new ImageViewModel((ImageView)view.findViewById(R.id.duplicate));

        upButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                FlightComponentListener current = currentListener.value();

                if(current != null){
                    current.onUpButtonPressed(flightPlanComponent);
                }

                return false;
            }
        });

        duplicateButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                FlightComponentListener current = currentListener.value();

                if(current != null){
                    current.onDuplicateButtonPressed(flightPlanComponent);
                }

                return false;
            }
        });

        downButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                FlightComponentListener current = currentListener.value();

                if(current != null){
                    current.onDownButtonPressed(flightPlanComponent);
                }

                return false;
            }
        });

        deleteButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                FlightComponentListener current = currentListener.value();

                if(current != null){
                    current.onDeleteButtonPressed(flightPlanComponent);
                }

                return false;
            }
        });

        attributeName.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                FlightComponentListener current = currentListener.value();

                if(current != null){
                    current.onTextPressed(flightPlanComponent);
                }

                return false;
            }
        });

        bindings.add(attributeName.text().bind(flightPlanComponent.getName().withDefault(flightPlanComponent.componentType().getName() + " : No Name")));
    }

    public void setListener(FlightComponentListener newListener){
        currentListener.set(newListener);
    }

    public void destroy(){
        bindings.remove();
    }
}
