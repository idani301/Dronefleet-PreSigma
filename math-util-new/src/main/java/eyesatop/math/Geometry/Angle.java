package eyesatop.math.Geometry;


import eyesatop.math.CircledMath;

/**
 * Created by Einav on 06/09/2017.
 */

public class Angle{

    private final CircledMath angle;

    private Angle(double angle) {
        this.angle = new CircledMath(angle,360);
    }

    public Angle(Angle angle){
        this.angle = new CircledMath(angle.degree(),360);
    }

    public static Angle angleRadian(double angle){
        return new Angle(Math.toDegrees(angle));
    }

    public static Angle zero(){
        return new Angle(0);
    }
    
    public static Angle halfCircle(){
        return new Angle(180);
    }


    
    public static Angle QuarterCircle(){
        return new Angle(90);
    }

    public static Angle angleDegree(double angle){
        return new Angle(angle);
    }

    public Angle add(Angle var){
        return new Angle(angle.add(var.degree()).getX());
    }

    public Angle sub(Angle var){
        return new Angle(angle.sub(var.degree()).getX());
    }

    public Angle multiple(Angle var){
        return new Angle(angle.multiple(var.degree()).getX());
    }

    public Angle divide(Angle var){
        return new Angle(angle.divide(var.degree()).getX());
    }

    public double sin(){
        return Math.sin(radian());
    }

    public Angle negetive(){
        return new Angle(-angle.getX());
    }

    public double radian(){
        return Math.toRadians(angle.getX());
    }

    public double degree(){
        return angle.getX();
    }

    public Angle distanceFromAngle(Angle angle){
        Angle temp = sub(angle);
        if (temp.angle.getX() < 180)
            return temp;
        return temp.complement();
    }

    public Angle complement(){
        return new Angle(360 - angle.getX());
    }

    @Override
    public String toString() {
        return "Angle{" +
                "angle=" + degree() +
                '}';
    }
}
