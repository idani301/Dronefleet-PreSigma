package eyesatop.controller.mission;

import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 03/09/2017.
 */

public class MissionTaskInfo {

    private final StubDroneTask stubDroneTask;
    private final boolean neccesseryForFinish;
    private final boolean allowedToCrush;
    private final Mission missionToRunOnFail;

    public MissionTaskInfo(StubDroneTask stubDroneTask,
                           boolean neccesseryForFinish,
                           boolean allowedToCrush,
                           Mission missionToRunOnFail) {
        this.stubDroneTask = stubDroneTask;
        this.neccesseryForFinish = neccesseryForFinish;
        this.allowedToCrush = allowedToCrush;
        this.missionToRunOnFail = missionToRunOnFail;
    }

    public StubDroneTask getStubDroneTask() {
        return stubDroneTask;
    }

    public boolean isNeccesseryForFinish() {
        return neccesseryForFinish;
    }

    public boolean isAllowedToCrush() {
        return allowedToCrush;
    }

    public Mission getMissionToRunOnFail() {
        return missionToRunOnFail;
    }
}
