package eyesatop.math;

import java.util.Arrays;

/**
 * Created by Einav on 17/07/2017.
 */

public class QuantaPoint {

    private final int[] x;

    public QuantaPoint(int... x) {
        this.x = x;
    }

    public QuantaPoint(int n, int m) {
        x = new int[2];
        x[0] = n;
        x[1] = m;
    }

    public int getN() throws MathException {
        if (x.length < 1)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        return x[0];
    }

    public int getM() throws MathException {
        if (x.length < 2)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        return x[1];
    }

    public int[] getX() {
        return x;
    }

    @Override
    public String toString() {
        return "QuantaPoint{" +
                "x=" + Arrays.toString(x) +
                '}';
    }
}
