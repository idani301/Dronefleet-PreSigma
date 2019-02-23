package eyesatop.util.dtmflight;

import java.util.ArrayList;

import eyesatop.math.Geometry.Point2D;

public class Point2DArrayList extends ArrayList<Point2D>{

    public Point2D getMaxY(){
        double maxY = get(0).getY();
        int index = 0;
        for (int i = 1; i < size(); i++) {
            if (get(i).getY() > maxY){
                maxY = get(i).getY();
                index = i;
            }
        }
        return get(index);
    }

}

