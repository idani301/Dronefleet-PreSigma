package eyesatop.unit.ui.models.map;

import com.google.android.gms.maps.GoogleMap;

import java.util.Map;

import eyesatop.util.Consumer;
import eyesatop.util.Removable;

public class RemoveMarker implements Consumer<GoogleMap> {

    private final Map<MapItem, Removable> bindings;
    private final MapItem mapItem;

    public RemoveMarker(Map<MapItem, Removable> bindings, MapItem mapItem) {
        this.bindings = bindings;
        this.mapItem = mapItem;
    }

    @Override
    public void apply(GoogleMap result) {
        Removable removable = bindings.remove(mapItem);
        if (removable != null) {
            removable.remove();
        }
    }
}
