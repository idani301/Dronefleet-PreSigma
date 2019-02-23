package eyesatop.util.geo.dtm;

import java.util.Collections;
import java.util.List;

import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 12/05/2018.
 */

public class DtmProviderWrapper implements DtmProvider {

    private final Property<Double> raiseValue = new Property<>(0D);

    private final DtmProvider mainProvider;
    private final DtmProvierASC semiProvider;

    public DtmProviderWrapper(DtmProvider mainProvider,DtmProvierASC semiProvider) {
        this.mainProvider = mainProvider;
        this.semiProvider = semiProvider;
    }

    public DtmProvider getMainProvider() {
        return mainProvider;
    }

    public DtmProvierASC getSemiProvider() {
        return semiProvider;
    }

    @Override
    public ObservableValue<Double> dtmRaiseValue() {
        return raiseValue;
    }

    @Override
    public void raiseDTM(double value) {
        raiseValue.set(raiseValue.value() + value);
    }

    @Override
    public void lowerDTM(double value) {
        raiseValue.set(raiseValue.value() - value);
    }

    @Override
    public void clearRaiseValue() {
        raiseValue.set(0D);
    }

    @Override
    public DtmProvider duplicate() {
        return this;
    }

    @Override
    public double density() {
        return mainProvider.density();
    }

    @Override
    public double terrainAltitude(Location location) throws TerrainNotFoundException {
        if(semiProvider != null) {
            try{
                return semiProvider.terrainAltitude(location) + raiseValue.value();
            }
            catch (TerrainNotFoundException e){
                return mainProvider.terrainAltitude(location) + raiseValue.value();
            }
        }

        return mainProvider.terrainAltitude(location) + raiseValue.value();
    }

    @Override
    public double terrainAltitude(double lat, double lon) throws TerrainNotFoundException {

        if(semiProvider != null) {

            try{
                return semiProvider.terrainAltitude(lat,lon) + raiseValue.value();
            }
            catch (TerrainNotFoundException e){
                return mainProvider.terrainAltitude(lat, lon) + raiseValue.value();
            }
        }
        return mainProvider.terrainAltitude(lat, lon) + raiseValue.value();
    }

    @Override
    public double maxTerrainAltitudeInArea(Location location, double areaSquareSide) throws TerrainNotFoundException {

        try{
            return semiProvider.maxTerrainAltitudeInArea(location,areaSquareSide);
        }
        catch (TerrainNotFoundException e){
            return mainProvider.maxTerrainAltitudeInArea(location,areaSquareSide);
        }
    }

    @Override
    public List<Location> corners() {
        if(semiProvider != null) {
            return semiProvider.corners();
        }
        else{
            return Collections.emptyList();
        }
    }

    @Override
    public double maxSteps() {
        return mainProvider.maxSteps();
    }

    @Override
    public double stepDistanceInMeters() {

        if(semiProvider != null){
            return semiProvider.stepDistanceInMeters();
        }

        return mainProvider.stepDistanceInMeters();
    }
}
