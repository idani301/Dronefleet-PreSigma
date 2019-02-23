package eyesatop.unit.ui.models.map;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

/**
 * Created by Idan on 18/11/2017.
 */

public class MapPolyline implements MapItem {

    private final ObservableList<Location> waypoints;

    private Polyline line;
    private final BooleanProperty visibility = new BooleanProperty(true);
    private final BooleanProperty isClosedShape = new BooleanProperty(false);
    private final Property<Integer> color;

    public MapPolyline(ObservableList<Location> waypoints, int color) {
        this.waypoints = waypoints;
        this.color = new Property<>(color);
    }

    @Override
    public Removable addToMap(GoogleMap map, Context context) {

        PolylineOptions options = new PolylineOptions().width(1).color(color.withDefault(Color.GREEN).value()).geodesic(true).zIndex(1F).width(6f);

        line = map.addPolyline(options);

        updateLine();

        return new RemovableCollection(

                new Removable() {
                    @Override
                    public void remove() {
                        line.remove();
                    }
                },
                isClosedShape.observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updateLine();
                    }
                }),
                waypoints.observe(new CollectionObserver<Location>(){
                    @Override
                    public void added(Location value, Observation<Location> observation) {
                        updateLine();
                    }

                    @Override
                    public void removed(Location value, Observation<Location> observation) {
                        updateLine();
                    }
                },UI_EXECUTOR),
                color.withDefault(Color.GREEN).observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                        line.setColor(newValue);
                    }
                },UI_EXECUTOR),
                visibility.observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        line.setVisible(newValue);
                    }
                },UI_EXECUTOR).observeCurrentValue()
        );
    }

    private void updateLine(){
        if(waypoints.size() <=1){
            line.setPoints(Collections.EMPTY_LIST);
            return;
        }

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for(Location location : waypoints){
            latLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));
        }

        if(isClosedShape.value()) {
            latLngs.add(new LatLng(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude()));
        }

        line.setPoints(latLngs);
    }

    public BooleanProperty getVisibility() {
        return visibility;
    }

    @Override
    public MapItemType type() {
        return MapItemType.POLYLINE;
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

    public BooleanProperty getIsClosedShape() {
        return isClosedShape;
    }

    public ObservableList<Location> getWaypoints() {
        return waypoints;
    }

    public Property<Integer> getColor() {
        return color;
    }
}
