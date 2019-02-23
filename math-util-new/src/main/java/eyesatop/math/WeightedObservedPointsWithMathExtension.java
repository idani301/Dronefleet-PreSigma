package eyesatop.math;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Einav on 04/05/2017.
 */

public class WeightedObservedPointsWithMathExtension extends WeightedObservedPoints{

    public WeightedObservedPointsWithMathExtension(){
        super();
    }


    public void addList(List<WeightedObservedPoint> weightedObservedPoints){

        for (WeightedObservedPoint weightedObservedPoint: weightedObservedPoints) {
            add(weightedObservedPoint);
        }

    }

    public WeightedObservedPointsWithMathExtension sortByX(){
        List<WeightedObservedPoint> weightedObservedPoints = toList();
        Collections.sort(weightedObservedPoints, new Comparator<WeightedObservedPoint>() {
            @Override
            public int compare(WeightedObservedPoint o1, WeightedObservedPoint o2) {
                if(o1.getX() > o2.getX())
                    return 1;
                if (o1.getX() < o2.getX())
                    return -1;
                return 0;
            }
        });
        WeightedObservedPointsWithMathExtension weightedObservedPointsWithMathExtension = new WeightedObservedPointsWithMathExtension();
        weightedObservedPointsWithMathExtension.addList(weightedObservedPoints);
        return weightedObservedPointsWithMathExtension;
    }

    public WeightedObservedPointsWithMathExtension sortByY(){
        List<WeightedObservedPoint> weightedObservedPoints = toList();
        Collections.sort(weightedObservedPoints, new Comparator<WeightedObservedPoint>() {
            @Override
            public int compare(WeightedObservedPoint o1, WeightedObservedPoint o2) {
                if(o1.getY() < o2.getY())
                    return 1;
                if (o1.getY() > o2.getY())
                    return -1;
                return 0;
            }
        });
        WeightedObservedPointsWithMathExtension weightedObservedPointsWithMathExtension = new WeightedObservedPointsWithMathExtension();
        weightedObservedPointsWithMathExtension.addList(weightedObservedPoints);
        return weightedObservedPointsWithMathExtension;
    }


    public double getMaxDifferenceInY(){
        return getMaxYPoint().getY() - getMinYPoint().getY();
    }

    public double getMaxDifferenceInX(){
        return getMaxYPoint().getX() - getMinYPoint().getX();
    }

    public WeightedObservedPoint getMaxYPoint(){
        if(toList().size() == 0)
            return null;
        double maxY = toList().get(0).getY();
        int maxIndex = 0;
        for(int i = 1; i < toList().size(); i++) {
            if (toList().get(i).getY() > maxY) {
                maxY = toList().get(i).getY();
                maxIndex = i;
            }
        }
        return toList().get(maxIndex);
    }

    public WeightedObservedPoint getMinYPoint(){
        if(toList().size() == 0)
            return null;
        double minY = toList().get(0).getY();
        int minIndex = 0;
        for(int i = 1; i < toList().size(); i++) {
            if (toList().get(i).getY() < minY) {
                minY = toList().get(i).getY();
                minIndex = i;
            }
        }
        return toList().get(minIndex);
    }

    public WeightedObservedPoint getMaxXPoint(){
        if(toList().size() == 0)
            return null;
        double maxX = toList().get(0).getX();
        int maxIndex = 0;
        for(int i = 1; i < toList().size(); i++) {
            if (toList().get(i).getX() > maxX) {
                maxX = toList().get(i).getX();
                maxIndex = i;
            }
        }
        return toList().get(maxIndex);
    }

    public WeightedObservedPoint getMinXPoint(){
        if(toList().size() == 0)
            return null;
        double minX = toList().get(0).getX();
        int minIndex = 0;
        for(int i = 1; i < toList().size(); i++) {
            if (toList().get(i).getX() < minX) {
                minX = toList().get(i).getX();
                minIndex = i;
            }
        }
        return toList().get(minIndex);
    }

    public double getmeanMaxMinY(){

        double max = getMaxYPoint().getY();
        double min = getMinYPoint().getY();

        return (max + min)/2;
    }

    @Override
    public String toString() {
        String result = "Points: \n";
        for (int i = 0; i < toList().size(); i++) {
            result += toList().get(i).getX() + "," + toList().get(i).getY() + "\n";
        }
        return result;
    }
}
