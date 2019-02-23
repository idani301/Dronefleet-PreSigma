package eyesatop.unit.ui.models.map.mission;

import java.util.List;

import eyesatop.unit.ui.models.missionplans.components.AttributeData;
import eyesatop.util.Removable;

/**
 * Created by Idan on 21/04/2018.
 */

public class AttributeDataResult {
    private final List<AttributeData> attributeDataList;
    private final Removable bindings;

    public AttributeDataResult(List<AttributeData> attributeDataList, Removable bindings) {
        this.attributeDataList = attributeDataList;
        this.bindings = bindings;
    }

    public List<AttributeData> getAttributeDataList() {
        return attributeDataList;
    }

    public Removable getBindings() {
        return bindings;
    }
}
