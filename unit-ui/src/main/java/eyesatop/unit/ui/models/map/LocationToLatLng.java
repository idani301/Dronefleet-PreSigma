package eyesatop.unit.ui.models.map;

import com.google.android.gms.maps.model.LatLng;

import eyesatop.util.Function;
import eyesatop.util.geo.Location;

public class LocationToLatLng implements Function<Location,LatLng> {
    @Override
    public LatLng apply(Location input) {

        if(input == null){
            return null;
        }

        return new LatLng(input.getLatitude(), input.getLongitude());
    }
}
