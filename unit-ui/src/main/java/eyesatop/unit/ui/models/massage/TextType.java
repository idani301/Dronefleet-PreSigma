package eyesatop.unit.ui.models.massage;

import java.util.HashMap;

/**
 * Created by Einav on 04/07/2017.
 */

public class TextType {


    public enum DefaultTextType {
        menu_text_type,
        menu_header_text_type,
    }

    private HashMap<DefaultTextType,Float> defaultTextSize = new HashMap<>();

    public TextType() {

        for (DefaultTextType textType: DefaultTextType.values()) {
            switch (textType){
                case menu_text_type:
                    defaultTextSize.put(DefaultTextType.menu_text_type,new Float(15));
                    break;
                case menu_header_text_type:
                    defaultTextSize.put(DefaultTextType.menu_header_text_type,new Float(17));
                    break;
            }
        }
    }

    public float getDefaultTextType(DefaultTextType defaultTextType){
        return defaultTextSize.get(defaultTextType);
    }
}
