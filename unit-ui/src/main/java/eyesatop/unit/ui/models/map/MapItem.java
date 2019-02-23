package eyesatop.unit.ui.models.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;

/**
 * Created by einav on 07/05/2017.
 */
public interface MapItem {

    public static float Z_INDEX_LOW = 1f;
    public static float Z_INDEX_MED = 2f;
    public static float Z_INDEX_HIGH = 3f;


    Removable addToMap(GoogleMap map,Context context);

    MapItemType type();

    void zoomChange(Float newZoom);

    ObservableValue<MapClickListener> mapListener();

    boolean isLocationHitItem(Location location);

    public interface MapClickListener {
        void onMapClick();
    }
}
