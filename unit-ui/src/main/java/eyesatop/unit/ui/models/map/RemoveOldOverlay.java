package eyesatop.unit.ui.models.map;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.Marker;

import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class RemoveOldOverlay implements Observer<GroundOverlay> {
    @Override
    public void observe(GroundOverlay oldValue, GroundOverlay newValue, Observation<GroundOverlay> observation) {
        if (oldValue != null) {
            oldValue.remove();
        }
    }
}
