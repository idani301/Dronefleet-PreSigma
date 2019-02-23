package eyesatop.util.drone;

import static eyesatop.util.drone.DroneVendor.*;

public enum DroneModel {
    UNKNOWN(EYESATOP),
    NOT_SUPPORTED(DJI),
    PHANTOM_3(DJI),
    PHANTOM_4(DJI),
    MAVIC(DJI),
    MAVIC_2(DJI),
    MATRCIE_200(DJI),
    M_600(DJI),
    MATRICE100(DJI),
    H480(YUNEEC),
    SIMULATOR(EYESATOP);

    private final DroneVendor vendor;

    DroneModel(DroneVendor vendor) {
        this.vendor = vendor;
    }

    public DroneVendor getVendor() {
        return vendor;
    }
}
