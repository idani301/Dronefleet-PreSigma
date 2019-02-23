package eyesatop.IdansFunctions;

import eyesatop.math.MathFunction;
import eyesatop.math.Polynom;

/**
 * Created by Einav on 15/05/2017.
 */

public class DroneFleetFunction {

    public static final double MAX_ROLL_PITCH_VELOCITY = 15;

    public double calcRollPitchSpeed(double distance, Velocities velocities){

        MathFunction mathFunction = new Polynom(new double[]{1,1});
        double velocity = velocities.getVelocity().getSphereRadius();

        double value = mathFunction.value(velocity);

        if(distance >= 2*MAX_ROLL_PITCH_VELOCITY){
            if(value > 15)
                return MAX_ROLL_PITCH_VELOCITY;
            return value;
        }
        if (value < distance/2) {
            return value;
        }
        return distance/2;

    }

}
