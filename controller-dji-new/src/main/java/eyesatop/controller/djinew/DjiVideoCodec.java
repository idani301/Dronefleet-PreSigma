package eyesatop.controller.djinew;

import android.app.Activity;
import android.graphics.SurfaceTexture;

import eyesatop.util.android.VideoCodec;

import dji.sdk.codec.DJICodecManager;

/**
 * Created by Idan on 07/11/2017.
 */

public class DjiVideoCodec implements VideoCodec {
    private DJICodecManager mCodecManager;

    @Override
    public void clean() {
        if(mCodecManager != null){
            try{
                mCodecManager.cleanSurface();
                mCodecManager.destroyCodec();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init(Activity activity, SurfaceTexture surfaceTexture, int width, int height) {
        mCodecManager = new DJICodecManager(activity, surfaceTexture, width, height);
    }

    @Override
    public void sendDataToDecoder(byte[] bytes, int size) {
        try {
            mCodecManager.sendDataToDecoder(bytes, size);
        }
        catch (Exception e){
        }
    }
}
