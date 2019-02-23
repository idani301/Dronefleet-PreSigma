package eyesatop.controller.tasks;

/**
 * Created by Idan on 24/10/2017.
 */

public class ConfirmationData {
    private final ConfirmationsType type;
    private final String info;

    public ConfirmationData(ConfirmationsType type, String info) {
        this.type = type;
        this.info = info;
    }

    public ConfirmationsType getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
