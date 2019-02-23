package eyesatop.unit.ui.models.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;

import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.android.functions.DrawableToBitmap;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

public class MapDrawable implements MapItem {

    private final Property<Location> location;
    private final Property<Drawable> drawable;
    private final BooleanProperty visibility;
    private final float zIndex;
    private final Property<MapClickListener> mapListener = new Property<>();

    public MapDrawable(float zIndex) {
        this.zIndex = zIndex;
        location = new Property<>();
        drawable = new Property<>();
        visibility = new BooleanProperty(true);
    }

    public MapDrawable(float zIndex,BooleanProperty visibility) {
        this.zIndex = zIndex;
        location = new Property<>();
        drawable = new Property<>();
        this.visibility = visibility;
    }

    public float getzIndex() {
        return zIndex;
    }

    public Property<Location> location() {
        return location;
    }

    public Property<Drawable> drawable() {
        return drawable;
    }

    private final Property<GroundOverlay> overlay = new Property<>();

    @Override
    public Removable addToMap(GoogleMap map,Context context) {


        return new RemovableCollection(
                overlay.bind(this.drawable()
                        .withDefault(new ColorDrawable(Color.argb(0, 0, 0, 0)))
                        .transform(new DrawableToBitmap(context))
                        .transform(new RescaleBitmap(40, 40))
                        .transform(new BitmapToDescriptor())
                        .transform(new CreateOverlay(
                                this.location()
                                        .transform(new LocationToLatLng()),
                                map, this.getzIndex(),visibility), UI_EXECUTOR)),

                overlay.observe(new RemoveOldOverlay(), UI_EXECUTOR),

                visibility.observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        GroundOverlay currentOverlay = overlay.value();
                        if(currentOverlay != null) {
                            currentOverlay.setVisible(newValue == null ? true : newValue);
                        }
                    }
                },UI_EXECUTOR).observeCurrentValue(),

                this.location()
                        .transform(new LocationToLatLng())
                        .observe(new SetOverlayLocation(overlay,drawable), UI_EXECUTOR),

                new Removable() {
                    @Override
                    public void remove() {
                        GroundOverlay lastOverlay = overlay.value();

                        if(lastOverlay != null){
                            lastOverlay.remove();
                        }

                        overlay.set(null);
                    }
                }
        );
    }

    @Override
    public MapItemType type() {
        return MapItemType.ICON;
    }

    @Override
    public void zoomChange(Float newZoom) {
        drawable.set(drawable.value());
    }

    @Override
    public Property<MapClickListener> mapListener() {
        return mapListener;
    }

    @Override
    public boolean isLocationHitItem(Location location) {
        GroundOverlay currentOverlay = overlay.value();
        if(currentOverlay == null){
            return false;
        }
        Location overlayLocation = location().value();
        if(overlayLocation == null){
            return false;
        }

        double width = currentOverlay.getWidth();
        double height = currentOverlay.getHeight();
        double squareSize = Math.max(width,height);

        double distance = overlayLocation.distance(location);
        return distance < squareSize;
    }

    public BooleanProperty getVisibility() {
        return visibility;
    }
}
