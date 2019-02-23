package eyesatop.controller.mission;

import eyesatop.controller.mission.exceptions.IteratorException;

/**
 * Created by Idan on 03/09/2017.
 */

public class MissionIterator {

    private MissionIteratorType iteratorType = MissionIteratorType.INCREASE;
    private int parameter = 1;

    public void setIteratorType(MissionIteratorType iteratorType) {
        this.iteratorType = iteratorType;
    }

    public MissionIteratorType getIteratorType() {
        return iteratorType;
    }

    public void applyIteratorCommand(IteratorCommandInfo info){

        if(info == null){
            return;
        }

        this.iteratorType = info.getIteratorType();
        this.parameter = info.getParameter();
    }

    public int calcNextIndex(int index,int missionSize) throws IteratorException {
        switch (iteratorType){

            case LOOP:
                Integer toReturn = index + parameter;
                if(toReturn >= missionSize){
                    return 0;
                }
                return toReturn;
            case INCREASE:
                return index+parameter;
            case DECREASE:
                return index-parameter;
            default:
                throw new IteratorException("Unknown iterator type");
        }
    }

}
