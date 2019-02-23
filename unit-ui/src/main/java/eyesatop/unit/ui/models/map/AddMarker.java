package eyesatop.unit.ui.models.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

import java.util.Map;

import eyesatop.util.Consumer;
import eyesatop.util.Removable;

public class AddMarker implements Consumer<GoogleMap> {

    private final Map<MapItem,Removable> bindings;
    private final MapItem mapItem;
    private final Context context;

    AddMarker(Map<MapItem, Removable> bindings, MapItem mapItem, Context context) {
        this.bindings = bindings;
        this.mapItem = mapItem;
        this.context = context;
    }

    @Override
    public void apply(GoogleMap map) {
        if (bindings.containsKey(mapItem)) {
            throw new IllegalStateException("Attempted to add the same map drawable twice");
        }
        bindings.put(mapItem,mapItem.addToMap(map,context));
    }
}
