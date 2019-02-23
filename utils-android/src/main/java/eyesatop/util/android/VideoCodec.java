package eyesatop.util.android;

import android.app.Activity;
import android.graphics.SurfaceTexture;

/**
 * Created by Idan on 07/11/2017.
 */

public interface VideoCodec {

    public static VideoCodec STUB = new VideoCodec() {
        @Override
        public void clean() {

        }

        @Override
        public void init(Activity activity, SurfaceTexture surfaceTexture, int width, int height) {

        }

        @Override
        public void sendDataToDecoder(byte[] bytes, int size) {

        }
    };

    public void clean();
    public void init(Activity activity, SurfaceTexture surfaceTexture,int width,int height);
    public void sendDataToDecoder(byte[] bytes,int size);
}
