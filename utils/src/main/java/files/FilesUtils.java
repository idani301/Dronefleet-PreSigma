package files;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Idan on 17/05/2018.
 */

public class FilesUtils {

    public static File[] listFiles(File directory, final String ending){
        if(directory == null || !directory.exists() || !directory.isDirectory()){
            return new File[0];
        }

        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                File sel = new File(dir, filename);

                if(sel.isDirectory()){
                    return false;
                }

                if(filename.contains("." + ending)){
                    return true;
                }

                return false;
            }

        };
        return directory.listFiles(filter);
    }
}
