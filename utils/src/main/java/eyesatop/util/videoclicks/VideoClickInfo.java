package eyesatop.util.videoclicks;

import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;

public class VideoClickInfo {

    private final Location location;
    private final int xPixel;
    private final int yPixel;
    private final int xMax;
    private final int yMax;
    private final GimbalState gimbalState;
    private final double time;

    public VideoClickInfo(Location location, int xPixel, int yPixel, int xMax, int yMax, GimbalState gimbalState, double time) {
        this.location = location;
        this.xPixel = xPixel;
        this.yPixel = yPixel;
        this.xMax = xMax;
        this.yMax = yMax;
        this.gimbalState = gimbalState;
        this.time = time;
    }

    public GimbalState getGimbalState() {
        return gimbalState;
    }

    public Location getLocation() {
        return location;
    }

    public int getxPixel() {
        return xPixel;
    }

    public int getyPixel() {
        return yPixel;
    }

    public int getxMax() {
        return xMax;
    }

    public int getyMax() {
        return yMax;
    }

    public boolean isAtCenter(){

        double xInPercent = 100D * (double)xPixel / (double)xMax;
        double yInPercent = 100D * (double)yPixel / (double)yMax;

        if(Math.abs(xInPercent - 50) <= 10 && Math.abs(yInPercent - 50) <= 10){
            return true;
        }

        return false;
    }

    public boolean isAtLimit(){

        double xInPercent = 100D * (double)xPixel / (double)xMax;
        double yInPercent = 100D * (double)yPixel / (double)yMax;

        if(xInPercent <= 20 || xInPercent >= 80){
            return true;
        }

        if(yInPercent <= 20 || yInPercent >= 80){
            return true;
        }

        return false;
    }

    public double getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "VideoClickInfo{" +
                "location=" + (location == null ? "N/A" : location.toString()) +
                ", xPixel=" + xPixel +
                ", yPixel=" + yPixel +
                ", xMax=" + xMax +
                ", yMax=" + yMax +
                '}';
    }
}
