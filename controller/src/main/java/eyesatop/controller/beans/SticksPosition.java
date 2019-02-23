package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SticksPosition {

    public static final SticksPosition ZEROS = new SticksPosition(
            new SingleStickPosition(0),
            new SingleStickPosition(0),
            new SingleStickPosition(0),
            new SingleStickPosition(0));

    protected static final String FORWARD = "forwardStick";
    protected static final String RIGHT = "rightStick";
    protected static final String VERTICAL = "verticalStick";
    protected static final String YAW = "yawStick";

    private final SingleStickPosition forwardStick;
    private final SingleStickPosition rightStick;
    private final SingleStickPosition verticalStick;
    private final SingleStickPosition yawStick;

    @JsonCreator
    public SticksPosition(@JsonProperty(FORWARD) SingleStickPosition forwardStick,
                          @JsonProperty(RIGHT) SingleStickPosition rightStick,
                          @JsonProperty(VERTICAL) SingleStickPosition verticalStick,
                          @JsonProperty(YAW) SingleStickPosition yawStick) {
        this.forwardStick = forwardStick;
        this.rightStick = rightStick;
        this.verticalStick = verticalStick;
        this.yawStick = yawStick;
    }

    @JsonProperty(FORWARD)
    public SingleStickPosition getForwardStick() {
        return forwardStick;
    }

    @JsonProperty(RIGHT)
    public SingleStickPosition getRightStick() {
        return rightStick;
    }

    @JsonProperty(VERTICAL)
    public SingleStickPosition getVerticalStick() {
        return verticalStick;
    }

    @JsonProperty(YAW)
    public SingleStickPosition getYawStick() {
        return yawStick;
    }

    @JsonIgnore
    public boolean isRelevant(){
        if(getForwardStick().getValueInPercent() != 0
                || getRightStick().getValueInPercent() != 0
                || getYawStick().getValueInPercent() != 0
                || getVerticalStick().getValueInPercent() != 0
                ){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SticksPosition{" +
                "forwardStick=" + forwardStick +
                ", rightStick=" + rightStick +
                ", verticalStick=" + verticalStick +
                ", yawStick=" + yawStick +
                '}';
    }
}
