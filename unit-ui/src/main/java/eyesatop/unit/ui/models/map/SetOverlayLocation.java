package eyesatop.unit.ui.models.map;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;

import eyesatop.util.geo.Location;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.model.Valued;

public class SetOverlayLocation implements Observer<LatLng> {

    private final Property<GroundOverlay> marker;
    private final Property<Drawable> drawable;

    public SetOverlayLocation(Property<GroundOverlay> marker, Property<Drawable> drawable) {
        this.marker = marker;
        this.drawable = drawable;
    }

    @Override
    public void observe(LatLng oldValue, LatLng newValue, Observation<LatLng> observation) {

        if(newValue == null){
            marker.set(null);
        }
        else {
            GroundOverlay overlayMarker = marker.value();
            if(overlayMarker != null) {
                overlayMarker.setPosition(newValue);
            }
            else{
                drawable.set(drawable.value());
            }
        }
    }
}
