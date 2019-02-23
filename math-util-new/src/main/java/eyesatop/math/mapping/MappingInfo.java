package eyesatop.math.mapping;

import java.util.ArrayList;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 12/07/2017.
 */

public class MappingInfo {

    private final ArrayList<Point3D> point3Ds;
//    private final ArrayList<ImageInfo> imageInfos;
    private final double height;

    public MappingInfo(ArrayList<Point3D> point3Ds, double minAzimuthDelta, double height) {
        this.point3Ds = point3Ds;
        this.height = height;
    }

    public MappingInfo(double height){
        point3Ds = new ArrayList<>();
        this.height = height;
    }

    public void addPoint(Point3D point3D){
        point3Ds.add(point3D);
    }

    public double getHeight() {
        return height;
    }

    public double numberOfImages(){
        return point3Ds.size();
    }

    public int numberOfPointsWithElevation(double elevation){
        int counter = 0;
        for (int i = 0; i < point3Ds.size(); i++) {
            if (Math.toRadians(point3Ds.get(i).getElevationDegree()) > elevation) ;
            counter++;
        }
        return counter;
    }

    public double numberOfImagesNotInTheSameAngle(double angleDegree){
        ArrayList<Angle> angles = new ArrayList<>();
        if (point3Ds.size() == 0)
            return 0;
        angles.add(Angle.angleDegree(point3Ds.get(0).getAzimuthDegree()));
        for (int i = 0; i < point3Ds.size(); i++) {
            Angle angle1 = Angle.angleDegree(point3Ds.get(i).getAzimuthDegree());
            boolean b = true;
            for (int j = 0; j < angles.size(); j++) {
                if (angle1.distanceFromAngle(angles.get(j)).degree() < angleDegree){
                    b = false;
                }
            }
            if (b){
                angles.add(angle1);
            }
        }
        return angles.size();
    }

    public double getShortestDistance(){
        if (point3Ds.size() == 0)
            return -1;
        double minRange = point3Ds.get(0).getSphereRadius();
        for (int i = 0; i < point3Ds.size(); i++) {
            if (minRange > point3Ds.get(i).getSphereRadius()){
                minRange = point3Ds.get(i).getSphereRadius();
            }
        }
        return minRange;
    }

}
