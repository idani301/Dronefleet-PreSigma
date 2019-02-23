package eyesatop.controller.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by einav on 15/05/2017.
 */
public class TaskProgressState {

    private static final String PROGRESS_PERCENT = "progressPercent";
    private static final String CURRENT_ACTION = "currentAction";

    private final int progressPercent;
    private final String currentAction;

    @JsonCreator
    public TaskProgressState(
            @JsonProperty(PROGRESS_PERCENT)
            int progressPercent,

            @JsonProperty(CURRENT_ACTION)
            String currentAction) {
        this.progressPercent = progressPercent;
        this.currentAction = currentAction;
    }

    @JsonProperty(PROGRESS_PERCENT)
    public int getProgressPercent() {
        return progressPercent;
    }

    @JsonProperty(CURRENT_ACTION)
    public String getCurrentAction() {
        return currentAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskProgressState that = (TaskProgressState) o;

        if (progressPercent != that.progressPercent) return false;
        return currentAction != null ? currentAction.equals(that.currentAction) : that.currentAction == null;

    }

    @Override
    public int hashCode() {
        int result = progressPercent;
        result = 31 * result + (currentAction != null ? currentAction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskProgressState{" +
                "progressPercent=" + progressPercent +
                ", currentAction='" + currentAction + '\'' +
                '}';
    }
}
