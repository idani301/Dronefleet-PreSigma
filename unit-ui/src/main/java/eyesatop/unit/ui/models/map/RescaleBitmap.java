package eyesatop.unit.ui.models.map;

import android.graphics.Bitmap;

import java.lang.ref.WeakReference;

import eyesatop.util.Function;

/**
 * Created by einav on 13/04/2017.
 */

public class RescaleBitmap implements Function<Bitmap, Bitmap> {

    private final int dstWidth;
    private final int dstHeight;

    private volatile WeakReference<Bitmap> lastSource;
    private volatile WeakReference<Bitmap> lastDestination;

    public RescaleBitmap(int dstWidth, int dstHeight) {
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;

        lastSource = new WeakReference<>(null);
        lastDestination = new WeakReference<>(null);
    }

    @Override
    public Bitmap apply(Bitmap input) {
        Bitmap dest = lastDestination.get();
        if (dest == null || !input.equals(lastSource.get())) {
            dest = Bitmap.createScaledBitmap(input, dstWidth, dstHeight, true);
        }
        return dest;
    }
}
