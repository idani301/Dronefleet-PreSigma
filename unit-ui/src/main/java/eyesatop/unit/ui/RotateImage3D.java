package eyesatop.unit.ui;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Created by Elnatan on 19-12-17.
 */

public class RotateImage3D extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private final boolean mReverse;
    private Camera mCamera;
    private ImageView image;

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair
     * of X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length
     * of the translation can be specified, as well as whether the translation
     * should be reversed in time.
     *
     */
    public RotateImage3D(ImageView imageView, float degrees, float depthZ) {

        this.image = imageView;
        mCenterX = image.getWidth() / 2.0f;
        mCenterY = image.getHeight() / 2.0f;

        mFromDegrees = degrees;
        mToDegrees = degrees;
        mDepthZ = depthZ;
        mReverse = true;


        setFillAfter(true);
        setInterpolator(new LinearInterpolator());
        //Monitor settings
        image.startAnimation(this);
    }


    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        //Save a camera initial state, for restoreToLastSnapshot()
        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        //Revolving around the Y axis degrees
        camera.rotateX(degrees);
        //Remove the matrix row camera, assigned to matrix
        camera.getMatrix(matrix);
        //Camera return to the initial state, to continue for the next calculation
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
