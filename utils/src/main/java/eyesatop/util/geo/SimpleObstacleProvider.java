package eyesatop.util.geo;

import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.ObservableList;

public class SimpleObstacleProvider implements ObstacleProvider{

    private final DtmProvider provider;
    private final ObservableList<ObstacleObject> obstacleObjects = new ObservableList<>();

    public SimpleObstacleProvider(DtmProvider provider) {
        this.provider = provider;
    }

    @Override
    public DtmProvider dtmProvider() {
        return provider;
    }

    @Override
    public ObservableList<ObstacleObject> obstacleObjects() {
        return obstacleObjects;
    }
}
