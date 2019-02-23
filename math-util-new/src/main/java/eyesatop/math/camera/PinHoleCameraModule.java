package eyesatop.math.camera;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 07/07/2017.
 */

public class PinHoleCameraModule implements CameraModule{


    private final Frame frame;

    private final double focalLength;

    public PinHoleCameraModule(Frame frame, double focalLength) {
        this.frame = frame;
        this.focalLength = focalLength;
    }

    public PinHoleCameraModule(CameraName cameraName){
//        this.frame = new Frame(4000,3000);
        switch (cameraName){
            case MAVIC:
                this.frame = new Frame(4000,3000);
                focalLength = 3050;
                break;
            case PHANTOM_4:
                this.frame = new Frame(5472,3078);
                focalLength = 3050;
                break;
            case MATRICE_100:
                this.frame = new Frame(new Pixel(961.75,543.75),1920,1080);
                focalLength = 1558.36;
                break;
            default:
                this.frame = new Frame(4000,3000);
                focalLength = 4500;
        }
    }

    @Override
    public Point3D getPoint3DFromPixelOpticalZoom(Pixel pixel, double zoom) {
        double teta = Math.atan(pixel.getRadius(frame)/(zoom*focalLength));
        double psai = pixel.getAngle(frame);

        return Point3D.cameraSpherePointOfView(teta,psai);
    }


    @Override
    public Pixel getPixelFromPoint3D(Point3D point3D) {
        double r = focalLength*Math.tan(point3D.getTetaCameraPointOfView());
        double phi = focalLength*Math.tan(point3D.getPsaiCameraPointOfView());

        return Pixel.RadialPixel(r,phi,frame.getCenter().getSize(),frame);
    }

    @Override
    public Point3D getPoint3DFromPixel(Pixel pixel) {
        double teta = Math.atan(pixel.getRadius(frame)/focalLength);
        double psai = pixel.getAngle(frame);
        return Point3D.cameraSpherePointOfView(teta,psai);
    }

    @Override
    public double EstimatedError(Pixel pixel) {
        double teta1 = Math.atan(pixel.getRadius(frame)/focalLength);
        double teta2 = Math.atan((pixel.getRadius(frame) + 1.41)/focalLength);

        return -teta1 + teta2;

    }

    @Override
    public Frame getFrame() {
        return frame;
    }
}
