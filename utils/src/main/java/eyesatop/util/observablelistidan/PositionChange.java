package eyesatop.util.observablelistidan;

/**
 * Created by Idan on 18/12/2017.
 */

public class PositionChange {
    private final int oldIndex;
    private final int newIndex;

    public PositionChange(int oldIndex, int newIndex) {
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }

    @Override
    public String toString() {
        return "PositionChange{" +
                "oldIndex=" + oldIndex +
                ", newIndex=" + newIndex +
                '}';
    }
}

