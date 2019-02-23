package eyesatop.imageprocessing;

/**
 * Created by Einav on 19/10/2017.
 */

public class ImageProcessingException extends Exception{

    public enum ImageProcessingExceptionType{
        END_OF_VIDEO_FILE;
    }

    private final ImageProcessingExceptionType imageProcessingExceptionType;

    public ImageProcessingException(ImageProcessingExceptionType imageProcessingExceptionType) {
        this.imageProcessingExceptionType = imageProcessingExceptionType;
    }

    public ImageProcessingExceptionType getImageProcessingExceptionType() {
        return imageProcessingExceptionType;
    }
}
