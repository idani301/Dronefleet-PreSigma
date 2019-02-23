package eyesatop.unit.ui.models.map;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

/**
 * Created by einav on 07/05/2017.
 */

public class MapCircle implements MapItem {

    private final BooleanProperty visible = new BooleanProperty(true);
    private final Property<Location> center = new Property<>();
    private final Property<Double> radius = new Property<>();
    private final Property<Integer> color = new Property<>(Color.argb(150, 0, 255, 0));
    private Circle googleCircle;

    @Override
    public Removable addToMap(GoogleMap map, Context context) {

        googleCircle = map.addCircle(new CircleOptions().strokeColor(color.value()).zIndex(1).strokeWidth(6f).center(new LatLng(0,0)).radius(100));
        googleCircle.setVisible(false);

        return new RemovableCollection(

                new Removable() {
                    @Override
                    public void remove() {
                        googleCircle.remove();
                    }
                },
                radius.observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        if(newValue != null) {
                            googleCircle.setRadius(newValue);
                        }
                        calcIsVisible(center.value(),newValue,visible.value());
                    }
                },UI_EXECUTOR).observeCurrentValue(),
                center.observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        if(newValue != null) {
                            googleCircle.setCenter(new LatLng(newValue.getLatitude(), newValue.getLongitude()));
                        }
                        calcIsVisible(newValue,radius.value(),visible.value());
                    }
                },UI_EXECUTOR).observeCurrentValue(),
                visible.observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        calcIsVisible(center.value(),radius.value(),newValue);
                    }
                },UI_EXECUTOR).observeCurrentValue()
        );
    }

    private void calcIsVisible(Location center,Double radius,Boolean visibility){
        boolean currentIsVisible = googleCircle.isVisible();
        boolean newIsVisible = center != null && radius != null && visibility != null && visibility;

        if(currentIsVisible != newIsVisible){
            googleCircle.setVisible(newIsVisible);
        }
    }

    public Property<Location> center() {
        return center;
    }

    public Property<Double> radius(){
        return radius;
    }

    public BooleanProperty visible(){
        return visible;
    }

    @Override
    public MapItemType type() {
        return MapItemType.CIRCLE;
    }

    public Property<Integer> getColor() {
        return color;
    }

    @Override
    public void zoomChange(Float newZoom) {
    }

    @Override
    public ObservableValue<MapClickListener> mapListener() {
        return null;
    }

    @Override
    public boolean isLocationHitItem(Location location) {
        return false;
    }

}
