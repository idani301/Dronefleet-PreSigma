package eyesatop.landingpad;

import eyesatop.util.geo.ControlAxes;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Bindable;
import eyesatop.util.model.Observable;

public interface Landing {
    Bindable<Telemetry> telemetry();
    Observable<ControlAxes> controlAxes();
    void close();
}
