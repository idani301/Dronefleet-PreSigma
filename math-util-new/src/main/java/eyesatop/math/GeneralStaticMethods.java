package eyesatop.math;

import Jama.Matrix;

/**
 * Created by Einav on 10/07/2017.
 */

public class GeneralStaticMethods {

    public static void printMatrix(Matrix A){
        double[][] a = A.getArray();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + " ,");
            }
            System.out.println("");
        }
    }

    public static String matrixToString(Matrix A){
        String result = "";
        double[][] a = A.getArray();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result += "" + a[i][j];
                if (j < a[0].length-1)
                    result += "\t\t";
            }
            result += "\n";
        }
        return result;
    }

}
