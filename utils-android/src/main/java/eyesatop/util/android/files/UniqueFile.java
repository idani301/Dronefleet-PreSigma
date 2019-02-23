package eyesatop.util.android.files;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Idan on 31/10/2017.
 */

public class UniqueFile {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm");

    private final String name;
    private final String finalName;
    private final File rootFolder;
    private boolean includeDate = false;

    public UniqueFile(String name, String finalName, File rootFolder) {
        this.name = name;
        this.finalName = finalName;
        this.rootFolder = rootFolder;
    }

    public void setIncludeDate(boolean includeDate) {
        this.includeDate = includeDate;
    }


    public File createUniqueFile() throws IOException{

        if(rootFolder == null){
            throw new IOException("root folder is NULL");
        }
        if(name == null){
            throw new IOException("name is NULL");
        }

        if(!rootFolder.exists()){
            boolean isCreated = rootFolder.mkdirs();
            if(!isCreated){
                throw new IOException("Unable to create root folder : " + rootFolder);
            }
        }
        String fileName = name;

        if(includeDate){
            fileName += "_" + dateFormat.format(new Date());
        }

        File fileToCreate = new File(rootFolder.getAbsolutePath() + "/" + fileName + finalName);

        if(!fileToCreate.exists()){
            boolean isCreated = finalName == "" ? fileToCreate.mkdirs() : fileToCreate.createNewFile();

            if(isCreated == false){
                throw new IOException("File was supposed to be created");
            }
            return fileToCreate;
        }

        int counter = 0;
        while(true){
            counter++;

            File tempFileToCreate = new File(fileToCreate.getAbsolutePath() + "_Number_" + counter + finalName);

            if(!tempFileToCreate.exists()){
                boolean isCreated = finalName == "" ? tempFileToCreate.mkdirs() : tempFileToCreate.createNewFile();

                if(isCreated == false){
                    throw new IOException("File was supposed to be created");
                }

                return tempFileToCreate;
            }

            if(counter > 1000){
                throw new IOException("Reached max tries to create path : " + fileToCreate.getAbsolutePath());
            }
        }
    }
}
