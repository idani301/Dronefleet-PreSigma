//package eyesatop.util.geo.dtm;
//
//import eyesatop.util.geo.Location;
//import eyesatop.util.geo.TerrainNotFoundException;
//
///**
// * Created by Einav on 06/11/2017.
// */
//
//public class PlaneDtmSimulator implements DtmProvider{
//
//    private final int numberOfObstacle;
//    private final double DtmSizeInMeter;
//    private final Location centerLocationDtm;
//    private final double density;
//
//    public PlaneDtmSimulator(int numberOfObstacle, double dtmSizeInMeter, Location centerLocationDtm, double density) {
//        this.numberOfObstacle = numberOfObstacle;
//        DtmSizeInMeter = dtmSizeInMeter;
//        this.centerLocationDtm = centerLocationDtm;
//        this.density = density;
//    }
//
//    @Override
//    public DtmProvider duplicate() {
//        return null;
//    }
//
//    @Override
//    public double density() {
//        return density;
//    }
//
//    @Override
//    public double terrainAltitude(Location location) throws TerrainNotFoundException {
//        return 0;
//    }
//
//    @Override
//    public double terrainAltitude(double lat, double lon) throws TerrainNotFoundException {
//        return 0;
//    }
//
//    @Override
//    public double maxSteps() {
//        return 0;
//    }
//
//    @Override
//    public double stepDistanceInMeters() {
//        return 0;
//    }
//}
