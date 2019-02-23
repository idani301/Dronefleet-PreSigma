package eyesatop.unit.ui.models.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import eyesatop.util.Function;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;
import eyesatop.util.model.Valued;

public class CreateOverlay implements Function<BitmapDescriptor, GroundOverlay> {
    private final Valued<LatLng> location;
    private final GoogleMap googleMap;
    private final float zIndex;
    private final Valued<Boolean> visible;

    public CreateOverlay(Valued<LatLng> location, GoogleMap googleMap, float zIndex,Valued<Boolean> visible) {
        this.location = location;
        this.googleMap = googleMap;
        this.zIndex = zIndex;
        this.visible = visible;
    }

    @Override
    public GroundOverlay apply(BitmapDescriptor input) {

        LatLng currentLocation = location.value();

        if(currentLocation == null){
            return null;
        }

        double s = 40075*1000 * Math.cos(Math.toRadians(currentLocation.latitude))/(Math.pow(2,googleMap.getCameraPosition().zoom+8));

        double s0 = 40075*1000 * Math.cos(0)/(Math.pow(2,googleMap.getCameraPosition().zoom+8));

        double s_s0 = s/s0;

        float overlaySize = (float) (Math.abs(s_s0) * 2.5 * Math.pow(2, 21 - googleMap.getCameraPosition().zoom));

        return googleMap.addGroundOverlay(new GroundOverlayOptions()
                    .image(input)
                    .anchor(.5f, .5f)
                    .zIndex(zIndex)
                    .clickable(false)
                    .visible(visible.value())
                    .position(currentLocation, overlaySize, overlaySize));

    }
}
