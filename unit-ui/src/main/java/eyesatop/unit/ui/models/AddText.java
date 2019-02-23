package eyesatop.unit.ui.models;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import eyesatop.util.Function;

/**
 * Created by einav on 25/05/2017.
 */

public class AddText implements Function<Drawable, Drawable> {

    private final Resources resources;
    private final String name;
    private final float textSize;
    private final int textColor;

    public AddText(Resources resources, String name, float textSize, int textColor) {
        this.resources = resources;
        this.name = name;
        this.textSize = textSize;
        this.textColor = textColor;
    }

    @Override
    public Drawable apply(Drawable input) {
        Bitmap bitmap = Bitmap.createBitmap(input.getIntrinsicWidth(), input.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setFakeBoldText(true);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);

        Rect textBounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), textBounds);

        canvas.drawText(name, bitmap.getWidth()/1.7f, (bitmap.getHeight()+textBounds.height()) / 2.9f, paint);

        return new LayerDrawable(new Drawable[] {
                input,
                new BitmapDrawable(resources, bitmap)
        });
    }
}

