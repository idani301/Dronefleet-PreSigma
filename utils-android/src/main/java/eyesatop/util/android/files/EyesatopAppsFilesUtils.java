package eyesatop.util.android.files;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import eyesatop.util.android.SourceFiles;
import eyesatop.util.geo.LatLon;
import eyesatop.util.serialization.Serialization;

public class EyesatopAppsFilesUtils {

    public enum FoldersType {
        MISSION_PLANS,
        ORTO_MBTILES,
        DTM_ASC,
        APP_LOGS,
        APP_CONFIGURATION,
        LICENSE,
        LOCATION_FIX,
        MISSION_SESSIONS;
    }

    private static final String MAIN_FOLDER_NAME = "Eyesatop";
    private static final String RESOURCE = "Assets";
    private static final String ORTO = "Ortophoto";
    private static final String ORTO_MBTILES = "Mbtiles";

    private static final String DTM = "DTM";
    private static final String DTM_ASC = "Asc";

    private static final String MISSION = "Mission";
    private static final String MISSION_PLANS = "Plans";
    private static final String MISSION_SESSIONS = "Uncompleted Sessions";

    private static final String APPS = "Applications";
    private static final String LOGS = "Logs";
    private static final String CONFIGURATION = "Configurations";

    private static final String LICENSE = "License";

    private static EyesatopAppsFilesUtils instance = null;

    private String appName = null;

    public static EyesatopAppsFilesUtils getInstance(){
        if(instance == null){
            instance = new EyesatopAppsFilesUtils();
        }
        return instance;
    }

    public void setApplicationName(String appName){
        this.appName = appName;
    }

    private File getFolder(FoldersType type,File mainFolder) throws IllegalStateException{
        switch (type){

            case MISSION_PLANS:
                return new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + MISSION + "/" + MISSION_PLANS);
            case ORTO_MBTILES:
                return new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + RESOURCE + "/" + ORTO + "/" + ORTO_MBTILES);
            case DTM_ASC:
                File ascFolder = new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + RESOURCE + "/" + DTM + "/" + DTM_ASC);
                if(!ascFolder.exists()){
                    ascFolder.mkdirs();
                }
                return new File(ascFolder.getAbsolutePath() + "/" + "DTM.asc");
            case APP_LOGS:
                if(appName == null) {
                    throw new IllegalStateException("No App name found");
                }
                return new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + APPS + "/" + appName + "/" + LOGS);
            case APP_CONFIGURATION:
                if(appName == null) {
                    throw new IllegalStateException("No App name found");
                }
                return new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + APPS + "/" + appName + "/" + CONFIGURATION);
            case LICENSE:
                File licenseFolder = new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + LICENSE);
                if(!licenseFolder.exists()){
                    licenseFolder.mkdirs();
                }
                return new File(licenseFolder.getAbsolutePath() + "/License.txt");
            case LOCATION_FIX:
                File locationFixFolder = new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + RESOURCE);
                if(!locationFixFolder.exists()){
                    locationFixFolder.mkdirs();
                }

                File locationFixFile = new File(locationFixFolder.getAbsolutePath() + "/LocationFix.txt");
                if(!locationFixFile.exists()){
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(locationFixFile);
                        fileOutputStream.write((Serialization.JSON.serialize(new LatLon(31.1,34.1)) + "\n").getBytes());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return locationFixFile;
            case MISSION_SESSIONS:
                return new File(mainFolder.getAbsolutePath() + "/" + MAIN_FOLDER_NAME + "/" + MISSION + "/" + MISSION_SESSIONS);
        }
        return null; 
    }

    public File getFolder(FoldersType type,boolean isFromSDCard){
        if(isFromSDCard){
            File mainSDCardFolder = SourceFiles.getExternalSdCardPath();
            if(mainSDCardFolder == null){
                return null;
            }
            return getFolder(type,mainSDCardFolder);
        }
        else{
            return getFolder(type,Environment.getExternalStorageDirectory());
        }
    }
}
