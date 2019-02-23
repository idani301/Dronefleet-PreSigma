package eyesatop.math;

/**
 * Created by Einav on 11/07/2017.
 */

public class MathException extends Exception {

    public enum MathExceptionCause {
        general,
        infinity,
        infinityMinus,
        zeroResult,
        notRectangle,
        notSquare,
        notPolygon,
        notSupported
    }

    private final MathExceptionCause mathExceptionCause;

    public MathException() { super();
        mathExceptionCause = MathExceptionCause.general;
    }

    public MathException(String message) { super(message);
        mathExceptionCause = MathExceptionCause.general;
    }

    public MathException(String message, Throwable cause) { super(message, cause);
        mathExceptionCause = MathExceptionCause.general;
    }

    public MathException(Throwable cause) { super(cause);
        mathExceptionCause = MathExceptionCause.general;
    }

    public MathException(MathExceptionCause mathExceptionCause){
        super();
//        System.err.print("reason for math exception: " + mathExceptionCause);
        this.mathExceptionCause = mathExceptionCause;
    }

    public MathExceptionCause getMathExceptionCause() {
        return mathExceptionCause;
    }

    //    public String getMessage(MathExceptionCause mathExceptionCause){
//        switch (mathExceptionCause){
//            case general:
//                return "general Math exception";
//            case infinity:
//                return "infinity Math Exception";
//            case zeroResult:
//                return "zeroResult Math Exception";
//        }
//        return null;
//    }

}
