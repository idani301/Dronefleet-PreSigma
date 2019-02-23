package eyesatop.controller.beans;

import java.util.Collection;

import eyesatop.controller.tasks.TaskCategory;

public class TaskBlocker {

    private final TaskBlockerType blockerType;
    private final Collection<TaskType> affectedTasks;
    private final Collection<TaskCategory> affectedCategories;

    public TaskBlocker(TaskBlockerType blockerType, Collection<TaskType> affectedComponents, Collection<TaskCategory> affectedCategories) {
        this.blockerType = blockerType;
        this.affectedTasks = affectedComponents;
        this.affectedCategories = affectedCategories;
    }

    public TaskBlockerType getBlockerType() {
        return blockerType;
    }

    public Collection<TaskType> getAffectedTasks() {
        return affectedTasks;
    }

    public Collection<TaskCategory> getAffectedCategories() {
        return affectedCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskBlocker that = (TaskBlocker) o;

        return blockerType == that.blockerType;
    }

    @Override
    public int hashCode() {
        int result = blockerType != null ? blockerType.hashCode() : 0;
        result = 31 * result + (affectedTasks != null ? affectedTasks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskBlocker{" +
                "blockerType='" + blockerType + '\'' +
                ", affectedTasks=" + affectedTasks +
                '}';
    }
}
