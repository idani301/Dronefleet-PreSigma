package eyesatop.unit.ui.models.map.mission;

import android.content.res.Resources;

import java.util.List;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.CameraActionType;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.flightplans.FlightPlanComponentType;
import eyesatop.controller.mission.flightplans.FlightPlanInfo;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 24/12/2017.
 */

public abstract class FlightPlanComponent<T extends FlightPlanInfo> {

    private final Property<T> snapshot = new Property<>();
    private final BooleanProperty visibleOnMap = new BooleanProperty(true);

    private final Property<Location> lastLocationBeforeComponent = new Property<>();
    private final Property<String> name = new Property<>(componentType().getName());
    private final Property<Double> velocity = new Property<>();
    private final Property<Integer> heading = new Property<>();
    private final Property<AltitudeInfo> altitudeInfo = new Property<>();

    private final Property<Integer> hoverTime = new Property<>();

    private final Property<CameraActionType> cameraActionType = new Property<>(CameraActionType.NONE);
    private final Property<Integer> shootPhotoInIntervalNumber = new Property<>();
    private final Property<Double> opticalZoomLevel = new Property<>();

    private final Property<Integer> gimbalPitch = new Property<>();

    public void startEdit(){
        snapshot.set(createSnapshot());
    }
    public boolean isModified(){
        T snapshotValue = snapshot.value();
        if(snapshotValue == null){
            return false;
        }
        else{
            return !snapshotValue.equals(createSnapshot());
        }
    }

    public abstract List<String> illegalFields();

    public void restore() throws Resources.NotFoundException{
        T currentSnapshot = snapshot.value();
        if(currentSnapshot != null) {
            restoreFromSnapshot(snapshot.value());
        }
        else{
            throw new Resources.NotFoundException("Can't find valid snapshot");
        }
    }

    public BooleanProperty visibleOnMap(){
        return visibleOnMap;
    }

    public Property<Location> lastLocationBeforeComponent(){
        return lastLocationBeforeComponent;
    }

    public abstract ObservableValue<Location> startLocation();
    public abstract ObservableValue<Location> endLocation();

    public abstract ObservableValue<Location> centerLocation();
    public abstract void relocate(Location newCenter);

    protected abstract T createSnapshot();
    protected abstract void restoreFromSnapshot(T snapshot);

    public abstract Removable addToMap(MapViewModel mapViewModel);

    public abstract List<MissionRow.MissionRowStub> getMissionRows() throws DroneTaskException;

    public abstract FlightPlanComponentType componentType();

    public abstract AttributeDataResult attributeDataList();

    public Property<AltitudeInfo> altitudeInfo(){
        return altitudeInfo;
    }

    public Property<Integer> gimbalPitch() {
        return gimbalPitch;
    }

    public Property<String> getName() {
        return name;
    }

    public abstract FlightPlanComponent<T> duplicate();

    public Property<Double> velocity() {
        return velocity;
    }

    public Property<Integer> hoverTime(){
        return hoverTime;
    }

    public Property<Integer> heading() {
        return heading;
    }

    public Property<Double> opticalZoomLevel() {
        return opticalZoomLevel;
    }

    public abstract void destroy();

    public Property<CameraActionType> cameraActionType() {
        return cameraActionType;
    }

    public Property<Integer> shootPhotoInIntervalNumber() {
        return shootPhotoInIntervalNumber;
    }

    public abstract double highestTerrainOnComponent(DtmProvider provider) throws TerrainNotFoundException;

    public double highestASLOnComponent(DtmProvider provider,Location homeLocation) throws TerrainNotFoundException {

        AltitudeInfo altitudeInfoValue = altitudeInfo().value();

        if(altitudeInfoValue == null){
            throw new TerrainNotFoundException("Unknown Altitude info of component");
        }

        switch (altitudeInfoValue.getAltitudeType()){

            case ABOVE_GROUND_LEVEL:
                return highestTerrainOnComponent(provider) + altitudeInfoValue.getValueInMeters();
            case ABOVE_SEA_LEVEL:
                return altitudeInfoValue.getValueInMeters();
            case FROM_TAKE_OFF_LOCATION:

                if(homeLocation == null){
                    throw new TerrainNotFoundException("From take off location but no home");
                }

                return provider.terrainAltitude(homeLocation) + altitudeInfoValue.getValueInMeters();
                default:
                    throw new TerrainNotFoundException("Unknown altitude info type : " + altitudeInfoValue.getAltitudeType());
        }
    }
}
