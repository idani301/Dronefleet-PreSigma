package eyesatop.unit.ui.models.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import eyesatop.unit.ui.specialfunctions.oblimapper.ObliCircle;
import eyesatop.unit.ui.specialfunctions.oblimapper.OblimapperMission;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 08/10/2017.
 */

public class MapOblimapper implements MapItem{

    private Removable centerObserver = Removable.STUB;
    private final Property<OblimapperMission> currentMission;

    private final ArrayList<Removable> mapCirclesRemovables = new ArrayList<>();

    public MapOblimapper(Property<OblimapperMission> currentMission){
        this.currentMission = currentMission;
    }

    @Override
    public Removable addToMap(final GoogleMap map, final Context context) {

        return new RemovableCollection(
                currentMission.observe(new Observer<OblimapperMission>() {
                    @Override
                    public void observe(OblimapperMission oldValue, final OblimapperMission newObliMission, Observation<OblimapperMission> observation) {

                        centerObserver.remove();
                        setup(newObliMission, map, context);

                        centerObserver = newObliMission.getCenter().observe(new Observer<Location>() {
                            @Override
                            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                                setup(newObliMission, map, context);
                            }
                        }).observeCurrentValue();
                    }
                }).observeCurrentValue(),
                centerObserver,
                new Removable() {
                    @Override
                    public void remove() {
                        for(Removable circleRemovable : mapCirclesRemovables){
                            circleRemovable.remove();
                        }
                        mapCirclesRemovables.clear();
                    }
                }
        );
    }

    private void setup(OblimapperMission mission,GoogleMap map,Context context){

        Location centerLocation = mission.getCenter().value();

        for(Removable circleRemovable : mapCirclesRemovables){
            circleRemovable.remove();
        }
        mapCirclesRemovables.clear();

        if(centerLocation != null){
            for(ObliCircle circle : mission.getCircles()){
                MapCircle mapCircle = new MapCircle();
                mapCircle.radius().set(circle.getRadius());
                mapCircle.center().set(centerLocation);
                mapCircle.visible().set(true);
                mapCirclesRemovables.add(mapCircle.addToMap(map,context));
            }
        }
    }

    @Override
    public MapItemType type() {
        return MapItemType.OBLIMAPPER;
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
