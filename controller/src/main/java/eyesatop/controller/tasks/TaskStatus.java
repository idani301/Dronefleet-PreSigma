package eyesatop.controller.tasks;

public enum TaskStatus {
    NOT_STARTED, RUNNING, CANCELLED, FINISHED, ERROR;

    public boolean isTaskDone(){
        switch (this) {

            case NOT_STARTED:
                return false;
            case RUNNING:
                return false;
            case CANCELLED:
                return true;
            case FINISHED:
                return true;
            case ERROR:
                return true;
            default:
                return true;
        }
    }
}
