package eyesatop.util.geo;

import eyesatop.util.model.ObservableList;

/**
 * Created by Idan on 17/10/2017.
 */

public interface ObstacleProvider {
    public eyesatop.util.geo.dtm.DtmProvider dtmProvider();
    public ObservableList<ObstacleObject> obstacleObjects();
}
