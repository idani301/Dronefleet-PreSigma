package eyesatop.util.geo;

/**
 * Created by Idan on 23/10/2017.
 */

public class TerrainNotFoundException extends Exception {

    private final String errorInfo;

    public TerrainNotFoundException() {
        errorInfo = "Unknown Reason";
    }

    public TerrainNotFoundException(String errorInfo){
        this.errorInfo = errorInfo;
    }

    public String getErrorInfo() {
        return errorInfo;
    }
}
