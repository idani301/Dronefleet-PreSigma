package eyesatop.unit.ui.models.missionplans.uicomponents;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.missionplans.components.Waypoint;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;

/**
 * Created by Idan on 14/11/2017.
 */

public class WaypointViewModel extends AbstractViewModel<View> {

    private final TextViewModel waypointName;
    private final ImageViewModel selfInformation;
    private final ImageViewModel selfRemoveWaypointButton;
    private final Waypoint waypoint;

    public WaypointViewModel(View waypointView,Waypoint waypoint){
        super(waypointView);
        waypointName = new TextViewModel((TextView) waypointView.findViewById(R.id.waypointName));
        selfInformation = new ImageViewModel((ImageView) waypointView.findViewById(R.id.waypointSelfInformationButton));
        selfRemoveWaypointButton = new ImageViewModel((ImageView) waypointView.findViewById(R.id.selfRemoveWaypointButton));
        this.waypoint = waypoint;

        bindToWaypoint(waypoint);
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public Removable bindToWaypoint(Waypoint waypoint){
        return new RemovableCollection(
                waypointName.text().bind(waypoint.getWaypointName())
        );
    }

    public void setSelfInformationSingleTap(Function<MotionEvent,Boolean> function){
        selfInformation.singleTap().set(function);
    }

    public void setSelfRemoveSingleTap(Function<MotionEvent,Boolean> function){
        selfRemoveWaypointButton.singleTap().set(function);
    }
}
