package eyesatop.util.android;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Idan on 21/05/2018.
 */

public class SourceFiles {

    private static SourceFiles instance;


    public static SourceFiles getInstance(){
        if(instance == null){
            instance = new SourceFiles();
        }
        return instance;
    }

    public static File getExternalSdCardPath() {
        String path = null;

        File sdCardFile = null;

        File mntFolder = new File("mnt");
        if(!mntFolder.exists()){
            return null;
        }

        List<String> sdCardPossiblePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard","sdcard1","sdcard2");


        for (String sdPath : sdCardPossiblePath) {
            File file = new File("/mnt/", sdPath);

            if (file.isDirectory() && file.exists()) {
                path = file.getAbsolutePath();
                break;
            }
        }

        if (path != null) {
            return new File(path);
        }
        else {
            return null;
        }
    }
}
