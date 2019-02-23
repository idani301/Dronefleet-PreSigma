package eyesatop.unit.ui.models.map;

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

public class AddComponentName implements Function<Drawable, Drawable> {

    private final Resources resources;
    private final String name;
    private final float textSize;

    public AddComponentName(Resources resources, String name, float textSize) {
        this.resources = resources;
        this.name = name;
        this.textSize = textSize;
    }

    @Override
    public Drawable apply(Drawable input) {

        if(input.getIntrinsicWidth() <= 0 || input.getIntrinsicHeight() <= 0){
            return input;
        }

        Bitmap bitmap = Bitmap.createBitmap(input.getIntrinsicWidth(), input.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize/1.7F);
        paint.setFakeBoldText(true);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);

        Rect textBounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), textBounds);

        canvas.drawText(name, bitmap.getWidth()/2f, (bitmap.getHeight()+textBounds.height()) / 2f, paint);

        return new LayerDrawable(new Drawable[] {
                input,
                new BitmapDrawable(resources, bitmap)
        });
    }
}
