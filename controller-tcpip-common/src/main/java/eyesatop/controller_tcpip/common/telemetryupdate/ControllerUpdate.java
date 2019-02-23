package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eyesatop.controller.mock.MockController;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public abstract class ControllerUpdate<T> {

    protected static final String VALUE = "value";

    private final T value;

    @JsonCreator
    public ControllerUpdate(@JsonProperty(VALUE) T value) {
        this.value = value;
    }

    @JsonProperty(VALUE)
    public T getValue() {
        return value;
    }

    @JsonIgnore
    public abstract void updateController(MockController controller);
}
