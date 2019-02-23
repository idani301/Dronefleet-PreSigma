package eyesatop.controllersimulatornew.beans;

public class SimulatorZoomSpec {
    private final int maxOpticalZoomFactor;
    private final double opticalZoomStep;
    private final int maxDigitalZoomFactor = 2;
    private final double digitalZoomStep = 1;

    public SimulatorZoomSpec(int maxOpticalZoomFactor, double opticalZoomStep) {
        this.maxOpticalZoomFactor = maxOpticalZoomFactor;
        this.opticalZoomStep = opticalZoomStep;
    }

    public int getMaxOpticalZoomFactor() {
        return maxOpticalZoomFactor;
    }

    public double getOpticalZoomStep() {
        return opticalZoomStep;
    }

    public int getMaxDigitalZoomFactor() {
        return maxDigitalZoomFactor;
    }

    public double getDigitalZoomStep() {
        return digitalZoomStep;
    }
}
