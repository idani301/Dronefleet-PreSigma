package eyesatop.math.camera;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 20/06/2017.
 */

public interface CameraModule {

    public Pixel getPixelFromPoint3D(Point3D point3D);

    public Point3D getPoint3DFromPixel(Pixel pixel);

    public Point3D getPoint3DFromPixelOpticalZoom(Pixel pixel,double opticalZoomFactor);

    public double EstimatedError(Pixel pixel);

    public Frame getFrame();

}
