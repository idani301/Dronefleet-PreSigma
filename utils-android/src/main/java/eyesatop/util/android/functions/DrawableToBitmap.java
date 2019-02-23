package eyesatop.util.android.functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import eyesatop.util.Function;

public class DrawableToBitmap implements Function<Drawable, Bitmap> {

    private final Context context;
    private volatile WeakReference<Drawable> lastDrawable;
    private volatile WeakReference<Bitmap> lastBitmap;

    public DrawableToBitmap(Context context) {
        this.context = context;
        lastDrawable = new WeakReference<>(null);
        lastBitmap = new WeakReference<>(null);
    }

    @Override
    public Bitmap apply(Drawable input) {
        if (input instanceof BitmapDrawable) {
            return ((BitmapDrawable)input).getBitmap();
        }

        Bitmap bitmap = lastBitmap.get();
        if (input.equals(lastDrawable.get()) && bitmap != null) {
            return bitmap;
        }

        if (input.getIntrinsicWidth() <= 0 || input.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(input.getIntrinsicWidth(), input.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        input.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        input.draw(canvas);

        lastDrawable = new WeakReference<>(input);
        lastBitmap = new WeakReference<>(bitmap);

        return bitmap;
    }
}
