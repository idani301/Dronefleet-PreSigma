package eyesatop.unit.ui.models.massage;

import android.graphics.Color;

/**
 * Created by Einav on 04/07/2017.
 */

public class Text {

    private final String text;
    private final float textSize;
    private final int color;
    private final int backgroundColor;

    public static TextType textType = new TextType();

    public Text(String text, float textSize, int color, int backgroundColor) {
        this.text = text;
        this.textSize = textSize;
        this.color = color;
        this.backgroundColor = backgroundColor;
    }

    public Text(String text, float textSize, int color) {
        this.text = text;
        this.textSize = textSize;
        this.color = color;
        this.backgroundColor = Color.TRANSPARENT;
    }

    public String getText() {
        return text;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getColor() {
        return color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
