package eyesatop.controller.mission;

public class IteratorCommandInfo {

    private final MissionIteratorType iteratorType;
    private final int parameter;

    public IteratorCommandInfo(MissionIteratorType iteratorType, int parameter) {
        this.iteratorType = iteratorType;
        this.parameter = parameter;
    }

    public MissionIteratorType getIteratorType() {
        return iteratorType;
    }

    public int getParameter() {
        return parameter;
    }
}