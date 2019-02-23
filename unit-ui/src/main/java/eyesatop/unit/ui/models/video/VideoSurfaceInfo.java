package eyesatop.unit.ui.models.video;

import android.graphics.SurfaceTexture;

/**
 * Created by einav on 25/04/2017.
 */
public class VideoSurfaceInfo {

    private final SurfaceTexture surfaceTexture;
    private final int width;
    private final int height;

    public VideoSurfaceInfo(SurfaceTexture surfaceTexture, int width, int height) {
        this.surfaceTexture = surfaceTexture;
        this.width = width;
        this.height = height;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
