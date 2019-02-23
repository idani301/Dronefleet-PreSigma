package eyesatop.controller.beans;

/**
 * Created by einav on 23/01/2017.
 */
public enum TakeOffState {
    WAITING_FOR_MOTORS,
    MOTORS_ON,
    FLYING,
    REACHED_MINIMAL_HEIGHT,
    APPROCHING_TARGET_HEIGHT,
    DONE
}
