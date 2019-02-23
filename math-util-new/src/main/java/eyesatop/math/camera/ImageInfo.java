package eyesatop.math.camera;

import java.util.ArrayList;

import eyesatop.math.Geometry.EarthGeometry.GeographicPolygon;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.MathException;
import eyesatop.math.Time;

/**
 * Created by Einav on 20/06/2017.
 */

public class ImageInfo {

    private final CameraModule cameraModule;
    private final RotationMatrix3D rotationMatrix3D;
    private final Location cameraLocation;
    private final Time time;

    public ImageInfo(CameraModule cameraModule, RotationMatrix3D rotationMatrix3D, Location cameraLocation, Time time) {
        this.cameraModule = cameraModule;
        this.rotationMatrix3D = rotationMatrix3D;
        this.cameraLocation = cameraLocation;
        this.time = time;
    }

    public ImageInfo(ImageInfo imageInfo, RotationMatrix3D rotationMatrix3D) {
        this.cameraModule = imageInfo.cameraModule;
        this.rotationMatrix3D = rotationMatrix3D;
        this.cameraLocation = imageInfo.cameraLocation;
        this.time = imageInfo.time;
    }


    public GeographicPolygon getEstimateFramePolygonOnPlato(double height){
        ArrayList<Location> locations = new ArrayList<>();
        ArrayList<Pixel> pixels = cameraModule.getFrame().getFrameBorderPixels();

        for (int i = 0; i < pixels.size(); i++) {
            Pixel pixel = pixels.get(i);
            Point3D direction = getLineOfSightFromPixel(pixel,1);
            double distance = (cameraLocation.Height() - height)/Math.tan(Math.toRadians(-direction.getElevationDegree()));
            locations.add(cameraLocation.findPosition(distance,direction.getAzimuthDegree(),height));
        }

        return new GeographicPolygon(locations);
    }

    public Point3D getLineOfSightFromPixel(Pixel pixel,double opticalZoom){
        return cameraModule.getPoint3DFromPixelOpticalZoom(pixel,opticalZoom).rotate(rotationMatrix3D);
    }

    private Pixel getOriginalPixel(Pixel pixel, Frame scaledFrame, double zoom) throws MathException {
        double scaleWidth = cameraModule.getFrame().getWidth()/scaledFrame.getWidth();
        double scaleHeight = cameraModule.getFrame().getHeight()/scaledFrame.getHeight();
        if (scaleWidth == 0 || scaleHeight == 0){
            throw new MathException(MathException.MathExceptionCause.general);
        }
        Pixel pixel1 = new Pixel(pixel.getU()*scaleWidth,pixel.getV()*scaleHeight,pixel.getSize());
        double radius = pixel1.getRadius(cameraModule.getFrame());
        double angle = pixel1.getAngle(cameraModule.getFrame());
        radius /= zoom;

        return Pixel.RadialPixel(radius,angle,pixel.getSize(),cameraModule.getFrame());
    }

    private Pixel getSyntheticPixel(Pixel pixel, Frame scaledFrame) throws MathException {
        double scaleWidth = cameraModule.getFrame().getWidth()/scaledFrame.getWidth();
        double scaleHeight = cameraModule.getFrame().getHeight()/scaledFrame.getHeight();
        if (scaleWidth == 0 || scaleHeight == 0){
            throw new MathException(MathException.MathExceptionCause.general);
        }
        return new Pixel(pixel.getU()/scaleWidth,pixel.getV()/scaleHeight,pixel.getSize());
    }

    public Point3D getLineOfSightFromPixel(Pixel pixel, Frame scaledFrame, double digitalZoom) throws MathException {
        Pixel originalPixel = getOriginalPixel(pixel,scaledFrame,digitalZoom);
        return getLineOfSightFromPixel(originalPixel,1);
    }

    public Point3D getLineOfSightFromPixelOpticalZoom(Pixel pixel, Frame scaledFrame, double digitalZoom,double opticalZoom) throws MathException {
        Pixel originalPixel = getOriginalPixel(pixel,scaledFrame,digitalZoom);
        return getLineOfSightFromPixel(originalPixel,opticalZoom);
    }

    public Pixel getPixelFromLineOfSight(Point3D lineOfSight){
        return cameraModule.getPixelFromPoint3D(lineOfSight.rotate(rotationMatrix3D.getInverseRotationMatrix()));
    }

    public Pixel getPixelFromLineOfSight(Point3D lineOfSight, Frame scaledFrame) throws MathException {
        Pixel pixel = getPixelFromLineOfSight(lineOfSight);
        return getSyntheticPixel(pixel,scaledFrame);
    }

    public Point3D getLineOfSightFromLocation(Location location){
        double deltaHeight = cameraLocation.Height() - location.Height();
        double horizontalDistance = location.distance(cameraLocation).get2dRadius();
        double elevation = Math.atan(deltaHeight/horizontalDistance);
        double azimuth = cameraLocation.distance(location).getAzimuth();

        return Point3D.ElevationAzimuthCameraOfView(elevation,azimuth);
    }

    public Point3D getLineOfSightFromCartesianLocation(Point3D location){

        Point3D cameraLocation = this.cameraLocation.getLocation();
        double deltaHeight = cameraLocation.getZ() - location.getZ();
        double horizontalDistance = location.horizontalDistance(cameraLocation);
        double elevation = Math.atan(deltaHeight/horizontalDistance);
        double azimuth = cameraLocation.getAzimuth() - location.getAzimuth();

        return Point3D.ElevationAzimuthCameraOfView(elevation,azimuth);
    }

    public CameraModule getCameraModule() {
        return cameraModule;
    }

    public RotationMatrix3D getRotationMatrix3D() {
        return rotationMatrix3D;
    }

    public Location getCameraLocation() {
        return cameraLocation;
    }

    public Time getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "\nImageFrame=" + cameraModule.getFrame() +
                ",\nrotationMatrix3D=" + rotationMatrix3D +
                ",\ncameraLocation=" + cameraLocation +
                ",\ntime=" + time +
                '}';
    }

    public String cameraCalibrationString(){
        return "ImageInfo{" +
                "\nImageFrame=" + cameraModule.getFrame() +
                ",\nrotationMatrix3D=" + rotationMatrix3D +
                ",\ncameraLocation=" + cameraLocation.getLocation().toStringCartesian() +
                '}';
    }
}
