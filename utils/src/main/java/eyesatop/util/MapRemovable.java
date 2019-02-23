package eyesatop.util;

import java.util.Map;

public class MapRemovable implements Removable {

    private final Map map;
    private final Object key;

    public MapRemovable(Map map, Object key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public void remove() {
        map.remove(key);
    }
}
