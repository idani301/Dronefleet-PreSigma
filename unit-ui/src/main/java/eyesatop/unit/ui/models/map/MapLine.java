package eyesatop.unit.ui.models.map;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * Created by einav on 08/05/2017.
 */

public class MapLine implements MapItem {

    private final Property<Location> startPoint = new Property<>();
    private final Property<Location> endPoint = new Property<>();
    private final BooleanProperty visibility = new BooleanProperty(true);

    private final Property<Integer> color = new Property<>(Color.argb(150, 0, 255, 0));

    private Polyline line;

    private final Property<Double> dash = new Property<>();
    private final Property<Double> gap = new Property<>();

    @Override
    public Removable addToMap(GoogleMap map, Context context) {

        PolylineOptions options = new PolylineOptions().width(1).color(color.value()).geodesic(true).zIndex(1).width(6f);

        line = map.addPolyline(options);
        line.setVisible(false);

        return new RemovableCollection(

                dash.observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        List<PatternItem> patternItems = getPatternItems();
                        line.setPattern(patternItems);
                    }
                },UI_EXECUTOR).observeCurrentValue(),
                gap.observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        List<PatternItem> patternItems = getPatternItems();
                        line.setPattern(patternItems);
                    }
                },UI_EXECUTOR).observeCurrentValue(),
                new Removable() {
                    @Override
                    public void remove() {
                        line.remove();
                    }
                },
                color.observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                        line.setColor(newValue);
                    }
                },UI_EXECUTOR),
                visibility.observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        line.setVisible(newValue == null ? true : newValue);
                    }
                },UI_EXECUTOR).observeCurrentValue(),
                startPoint.observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        updateLine();
                    }
                }, UI_EXECUTOR),
                endPoint.observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        updateLine();
                    }
                }, UI_EXECUTOR).observeCurrentValue()
        );
    }

    private void updateLine(){

        ArrayList<LatLng> newLatLngsArray = new ArrayList<>();
        Location startLocation = startPoint.value();
        Location endLocation = endPoint.value();

        if(startLocation != null){
            newLatLngsArray.add(new LatLng(startLocation.getLatitude(),startLocation.getLongitude()));
        }

        if(endLocation != null){
            newLatLngsArray.add(new LatLng(endLocation.getLatitude(),endLocation.getLongitude()));
        }

        line.setPoints(newLatLngsArray);
    }

    public Property<Location> getStartPoint() {
        return startPoint;
    }

    public Property<Location> getEndPoint() {
        return endPoint;
    }

    @Override
    public MapItemType type() {
        return MapItemType.LINE;
    }

    public Property<Integer> getColor() {
        return color;
    }

    public BooleanProperty getVisibility() {
        return visibility;
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

    private List<PatternItem> getPatternItems(){
        Double dashValue = dash.value();
        Double gapValue = gap.value();

        ArrayList<PatternItem> patterns = new ArrayList<>();
        if(dashValue != null){
            patterns.add(new Dash(dashValue.floatValue()));
        }

        if(gapValue != null){
            patterns.add(new Gap(gapValue.floatValue()));
        }

        if(patterns.size() > 0){
            return patterns;
        }
        else{
            return null;
        }
    }

    public Property<Double> getDash() {
        return dash;
    }

    public Property<Double> getGap() {
        return gap;
    }

    @Override
    public String toString() {
        return "MapLine{" +
                "startPoint=" + startPoint.toString() +
                ", endPoint=" + endPoint.toString() +
                ", visibility=" + visibility.toString() +
                ", color=" + color.toString() +
                ", dash=" + dash.toString() +
                ", gap=" + gap.toString() +
                '}';
    }
}
