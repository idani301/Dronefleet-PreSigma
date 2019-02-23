package eyesatop.math.mapping;

import java.util.ArrayList;

import eyesatop.drone.DroneTelemetry;
import eyesatop.drone.MicroDroneMovement;
import eyesatop.drone.MoveInCircle;
import eyesatop.drone.MoveToPoint;
import eyesatop.math.Geometry.EarthGeometry.GeographicPolygon;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.EarthGeometry.RadiatorPlanInfo;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.camera.CameraName;
import eyesatop.math.camera.ImageInfo;
import eyesatop.math.camera.PinHoleCameraModule;

/**
 * Created by Einav on 06/09/2017.
 */

public class MappingSimulator {

    private final AreaOfMappingPoints areaOfMappingPoints;
    private int numberOfImages = 0;
    private double time = 0;

    public MappingSimulator() throws Exception {
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(new Location(32,34.79));
        locations.add(new Location(32,34.8));
        locations.add(new Location(31.99,34.8));
        locations.add(new Location(31.99,34.79));
        GeographicPolygon geographicPolygon = new GeographicPolygon(locations);
        areaOfMappingPoints = new AreaOfMappingPoints(geographicPolygon);
    }

    public void startRadiatorPlan(RadiatorPlanInfo radiatorPlanInfo, DroneTelemetry droneTelemetry) throws Exception {
        MicroDroneMovement microDroneMovement = MicroDroneMovement.Mavic();
        ArrayList<Location> locations = radiatorPlanInfo.getRadiatorPlan().getLocations();
        MoveToPoint moveToFirstPoint = new MoveToPoint(radiatorPlanInfo.getHorizontalVelocity(),2,locations.get(0),0);
        while (!moveToFirstPoint.isTargetReached()) {
            droneTelemetry = droneTelemetry.goForwardInTime(microDroneMovement.speedsToDrone(moveToFirstPoint,droneTelemetry));
        }
        time += moveToFirstPoint.getTimeInTask();

        for (int i = 1; i < locations.size(); i++) {
            MoveToPoint moveToPoint = new MoveToPoint(radiatorPlanInfo.getHorizontalVelocity(),2,locations.get(i),0);
            while (!moveToPoint.isTargetReached()){
                droneTelemetry = droneTelemetry.goForwardInTime(microDroneMovement.speedsToDrone(moveToPoint,droneTelemetry));
                if ((10*moveToPoint.getTimeInTask()) % (int)(radiatorPlanInfo.getTimeLapse()*10) == 0){
                    numberOfImages++;
                    RotationMatrix3D rotationMatrix3D = RotationMatrix3D.Body3DNauticalAngles(0,Math.toRadians(90),0);
                    ImageInfo imageInfo = new ImageInfo(
                            new PinHoleCameraModule(CameraName.MAVIC),
                            rotationMatrix3D,
                            droneTelemetry.getLocation(),
                            null
                    );
                    areaOfMappingPoints.addNewImage(imageInfo);
                }
            }
            time += moveToPoint.getTimeInTask();
        }
    }

    public void startObliCircle(DroneTelemetry droneTelemetry, Location center, double velocity, double radius, boolean isLookIn, double elevation, double timeBetweenPhotos) throws Exception {
        MicroDroneMovement microDroneMovement = MicroDroneMovement.Mavic();

        double maxSpeed = microDroneMovement.getMaxHorizontalSpeed();
        int numberOfCircles = 1;
        MoveInCircle moveInCircle = new MoveInCircle(radius,center,velocity,true,maxSpeed,droneTelemetry, numberOfCircles);
        while (moveInCircle.getMoveInCircleStatus() != MoveInCircle.MoveInCircleStatus.FINISHED){
            droneTelemetry = droneTelemetry.goForwardInTime(microDroneMovement.speedsToDrone(moveInCircle,droneTelemetry));
            if (moveInCircle.getTimeInCircle()% timeBetweenPhotos == 0 && moveInCircle.getMoveInCircleStatus() == MoveInCircle.MoveInCircleStatus.MOVE_IN_CIRCLE){
                double angleFromCenter = (center.azimuth(droneTelemetry.getLocation()));
                numberOfImages++;
                RotationMatrix3D rotationMatrix3D = RotationMatrix3D.Body3DNauticalAngles(angleFromCenter,Math.toRadians(elevation),Math.toRadians(0));
                if (isLookIn) {
                    rotationMatrix3D = RotationMatrix3D.Body3DNauticalAngles((angleFromCenter + Math.PI), Math.toRadians(elevation), Math.toRadians(0));
                }
                ImageInfo imageInfo = new ImageInfo(
                        new PinHoleCameraModule(CameraName.MAVIC),
                        rotationMatrix3D,
                        droneTelemetry.getLocation(),
                        null
                );
                areaOfMappingPoints.addNewImage(imageInfo);
            }
        }

        time += moveInCircle.getTimeInMovement();


    }

    public void printResultToFile(int minNumberOfImages, double spaceBetweenAngleDegree, double height){
        System.out.println("present number of points with images: " + areaOfMappingPoints.getNumberOfPointsWithMoreThenNumberOfImages(minNumberOfImages,spaceBetweenAngleDegree)*100/areaOfMappingPoints.getNumberOfPoints() + "%");
        System.out.println("Area Covered: " + Math.sqrt(areaOfMappingPoints.getNumberOfPointsWithMoreThenNumberOfImages(minNumberOfImages,spaceBetweenAngleDegree))*areaOfMappingPoints.getDensity() + "(m^2)");
        System.out.println("number Of pictures taken: " + numberOfImages);
        areaOfMappingPoints.saveAsImage(spaceBetweenAngleDegree);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        areaOfMappingPoints.saveAsImageOfResolution(height);
    }

    public AreaOfMappingPoints getAreaOfMappingPoints() {
        return areaOfMappingPoints;
    }

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public double getTime() {
        return time;
    }
}
