package eyesatop.math.mapping;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.WeightedScalar;
import eyesatop.math.camera.CameraModule;

/**
 * Created by Einav on 17/11/2017.
 */

public class AreaOfMapping {

    private final PointOfMapping[][] pointsOfMapping;
    private final int width;
    private final int length;

    protected AreaOfMapping(PointOfMapping[][] pointsOfMapping) {
        this.pointsOfMapping = pointsOfMapping;
        width = pointsOfMapping.length;
        if (width != 0) {
            length = pointsOfMapping[0].length;
        } else {
            length = 0;
        }
    }

    public AreaOfMapping(int widthInMeters, int lengthInMeters, double density){
        width = (int) Math.round(widthInMeters/density);
        length = (int) Math.round(lengthInMeters/density);
        pointsOfMapping = new PointOfMapping[width][length];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                pointsOfMapping[i][j] = new PointOfMapping();
            }
        }
    }

    public Point3D calcCameraLocationToCoverAllPlatoArea(CameraModule cameraModule, double resolution, Angle maxElevation){
        return null;
    }

    public static double calcHeightFromResolution(double resolutionInMillimeters, WeightedScalar elevation){
        double resolution = resolutionInMillimeters/1000;
        double teta = Math.toRadians(90) - elevation.value();
        double beta = elevation.getAverageError()/2;
        double x1 = Math.tan(teta + beta);
        double x2 = Math.tan(teta - beta);
        return resolution/(x1 - x2);
    }

    public static double calcResolutionInMillimetersFromHeight(double height, WeightedScalar elevation){
        double teta = Math.toRadians(90) - elevation.value();
        double beta = elevation.getAverageError()/2;
        double x1 = Math.tan(teta + beta);
        double x2 = Math.tan(teta - beta);
        return height*(x1 - x2)*1000;
    }


}
