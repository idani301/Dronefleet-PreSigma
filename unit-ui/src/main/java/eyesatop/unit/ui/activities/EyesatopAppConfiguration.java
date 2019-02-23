package eyesatop.unit.ui.activities;

import android.app.Activity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.android.files.EyesatopAppsFilesUtils;
import eyesatop.util.geo.DistanceUnitType;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;

/**
 * Created by Idan on 12/12/2017.
 */

public class EyesatopAppConfiguration {

    private static final String CONFIGURATION_FILENAME = "configuration.txt";

    private final ExecutorService writingDataToFileExecutor = Executors.newSingleThreadExecutor();

    private static EyesatopAppConfiguration instance = new EyesatopAppConfiguration();
    private RemovableCollection bindToActivityRemovables = new RemovableCollection();

    private final Property<DistanceUnitType> appMeasureType = new Property<>(DistanceUnitType.METER);
    private final Property<Integer> takeoffAltitude = new Property<>(50);

    public static EyesatopAppConfiguration getInstance(){
        return instance;
    }

    private void createRootDirectory(){
        File rootFolder = EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.APP_CONFIGURATION ,false);
        if(!rootFolder.exists()){
            rootFolder.mkdirs();
        }
    }

    public void bindToActivity(final Activity activity){

        bindToActivityRemovables.remove();

        boolean initSuccessed = false;
        File configurationFile = getConfigurationFile(activity);

        if(configurationFile.exists()){
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(new FileInputStream(configurationFile));
                EyesatopAppConfigurationSavedClass savedClass = Serialization.JSON.deserialize((String)objectInputStream.readObject(),EyesatopAppConfigurationSavedClass.class);
                updateFromSavedClass(savedClass);
                initSuccessed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(objectInputStream != null){
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if(!initSuccessed) {
            writeDataToFile(activity);
        }

        bindToActivityRemovables.add(appMeasureType.observe(new Observer<DistanceUnitType>() {
            @Override
            public void observe(DistanceUnitType oldValue, DistanceUnitType newValue, Observation<DistanceUnitType> observation) {
                writeDataToFile(activity);
            }
        },writingDataToFileExecutor));

        bindToActivityRemovables.add(takeoffAltitude.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                writeDataToFile(activity);
            }
        }));
    }

    private void writeDataToFile(Activity activity){
        createRootDirectory();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(getConfigurationFile(activity)));
            EyesatopAppConfigurationSavedClass savedClass = toAppConfigurationSavedClass();
            objectOutputStream.writeObject(Serialization.JSON.serialize(savedClass));
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getConfigurationFile(Activity activity){
        return new File(EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.APP_CONFIGURATION,false) + "/" + CONFIGURATION_FILENAME);
    }

    public Property<Integer> getTakeoffAltitude() {
        return takeoffAltitude;
    }

    public Property<DistanceUnitType> getAppMeasureType() {
        return appMeasureType;
    }

    private EyesatopAppConfigurationSavedClass toAppConfigurationSavedClass(){
        return new EyesatopAppConfigurationSavedClass(
                appMeasureType.value(),
                takeoffAltitude.value());
    }

    private void updateFromSavedClass(EyesatopAppConfigurationSavedClass savedClass){
        this.appMeasureType.set(savedClass.getAppMeasureType());
        this.takeoffAltitude.set(savedClass.getTakeoffAltitude());
    }

    private static class EyesatopAppConfigurationSavedClass {

        private static final String APP_MEASURE_TYPE = "appMeasureType";
        private static final String TAKE_OFF_ALTITUDE = "takeoffAltitude";

        private final DistanceUnitType appMeasureType;
        private final Integer takeoffAltitude;

        @JsonCreator
        private EyesatopAppConfigurationSavedClass(
                @JsonProperty(APP_MEASURE_TYPE) DistanceUnitType appMeasureType,
                @JsonProperty(TAKE_OFF_ALTITUDE) Integer takeoffAltitude) {
            this.appMeasureType = appMeasureType;
            this.takeoffAltitude = takeoffAltitude;
        }

        @JsonProperty(TAKE_OFF_ALTITUDE)
        public Integer getTakeoffAltitude() {
            return takeoffAltitude;
        }

        @JsonProperty(APP_MEASURE_TYPE)
        public DistanceUnitType getAppMeasureType() {
            return appMeasureType;
        }
    }
}
