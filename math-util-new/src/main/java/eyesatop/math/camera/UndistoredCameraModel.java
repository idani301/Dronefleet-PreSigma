package eyesatop.math.camera;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.MathException;

/**
 * Created by Einav on 28/07/2017.
 */

public class UndistoredCameraModel {

    private final Point2D focalLength;
    private final Matrix rotationTranslationMatrix;
    private final Point2D centerPoint;

    private final double errorDet;

    public UndistoredCameraModel(Matrix mMatrix, double errorDet) throws MathException {
        this.errorDet = errorDet;
        if (mMatrix.getRowDimension() != 3)
            throw new MathException(MathException.MathExceptionCause.general);
        if (mMatrix.getRowDimension() != 4)
            throw new MathException(MathException.MathExceptionCause.general);

        double scale = Math.sqrt(mMatrix.get(2,0)*mMatrix.get(2,0) + mMatrix.get(2,1)*mMatrix.get(2,1) + mMatrix.get(2,2)*mMatrix.get(2,2));
        if (mMatrix.get(2,3) < 0) {
            scale *= -1;
        }

        mMatrix.times(1/scale);
        SingularValueDecomposition singularValueDecomposition = mMatrix.svd();
        singularValueDecomposition.getS();

        rotationTranslationMatrix = new Matrix(3,4);

        rotationTranslationMatrix.set(2,3,mMatrix.get(2,3));
        rotationTranslationMatrix.set(2,0,mMatrix.get(2,0));
        rotationTranslationMatrix.set(2,1,mMatrix.get(2,1));
        rotationTranslationMatrix.set(2,2,mMatrix.get(2,2));

        double centerX = (mMatrix.get(0,0)*mMatrix.get(2,0) + mMatrix.get(0,1)*mMatrix.get(2,1) + mMatrix.get(0,2)*mMatrix.get(2,2));
        double centerY = (mMatrix.get(1,0)*mMatrix.get(2,0) + mMatrix.get(1,1)*mMatrix.get(2,1) + mMatrix.get(1,2)*mMatrix.get(2,2));
        centerPoint = Point2D.cartesianPoint(centerX,centerY);

        double fx = Math.sqrt((mMatrix.get(0,0)*mMatrix.get(0,0) + mMatrix.get(0,1)*mMatrix.get(0,1) + mMatrix.get(0,2)*mMatrix.get(0,2)) - centerX*centerX);
        double fy = Math.sqrt((mMatrix.get(1,0)*mMatrix.get(1,0) + mMatrix.get(1,1)*mMatrix.get(1,1) + mMatrix.get(1,2)*mMatrix.get(1,2)) - centerY*centerY);
        focalLength = Point2D.cartesianPoint(fx,fy);

        rotationTranslationMatrix.set(0,0,(centerX*mMatrix.get(2,0) - mMatrix.get(0,0))/fx);
        rotationTranslationMatrix.set(0,1,(centerX*mMatrix.get(2,1) - mMatrix.get(0,1))/fx);
        rotationTranslationMatrix.set(0,2,(centerX*mMatrix.get(2,2) - mMatrix.get(0,2))/fx);

        rotationTranslationMatrix.set(1,0,(centerX*mMatrix.get(2,0) - mMatrix.get(1,0))/fy);
        rotationTranslationMatrix.set(1,1,(centerX*mMatrix.get(2,1) - mMatrix.get(1,1))/fy);
        rotationTranslationMatrix.set(1,2,(centerX*mMatrix.get(2,2) - mMatrix.get(1,2))/fy);

        rotationTranslationMatrix.set(0,3,(centerX*mMatrix.get(2,3) - mMatrix.get(0,3))/fx);
        rotationTranslationMatrix.set(1,3,(centerX*mMatrix.get(2,3) - mMatrix.get(1,3))/fy);
    }

    public Point2D getFocalLength() {
        return focalLength;
    }

    public Matrix getRotationTranslationMatrix() {
        return rotationTranslationMatrix;
    }

    public Point2D getCenterPoint() {
        return centerPoint;
    }

    public double getErrorDet() {
        return errorDet;
    }

    @Override
    public String toString() {
        return "UndistoredCameraModel{" +
                "focalLength=" + focalLength +
                ", rotationTranslationMatrix=" + rotationTranslationMatrix +
                ", centerPoint=" + centerPoint +
                ", errorDet=" + errorDet +
                '}';
    }
}
