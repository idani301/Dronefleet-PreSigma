//package eyesatop.unit.ui.models.specialfunctions;
//
//import android.app.Activity;
//import android.graphics.drawable.Drawable;
//import android.support.v4.content.ContextCompat;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import eyesatop.controller.DroneController;
//import eyesatop.unit.ui.R;
//import eyesatop.unit.ui.models.map.MapOblimapper;
//import eyesatop.unit.ui.models.map.MapViewModel;
//import eyesatop.unit.ui.models.tabs.SpecialFunctionType;
//import eyesatop.unit.ui.specialfunctions.oblimapper.ObliCircle;
//import eyesatop.unit.ui.specialfunctions.oblimapper.OblimapperMission;
//import eyesatop.util.Removable;
//import eyesatop.util.RemovableCollection;
//import eyesatop.util.android.FileExplorer.FileExplorer;
//import eyesatop.util.model.BooleanProperty;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by einav on 29/06/2017.
// */
//
//public class ObliFunction extends SpecialFunction {
//
//    private static final String MAIN_FOLDER = "SkyMapper";
//
//    private final FileExplorer fileExplorer;
//    private BooleanProperty isFunctionScreenOpened;
//    private BooleanProperty isObliMenuOpened = new BooleanProperty(false);
//
//    private final Property<OblimapperMission> currentMission = new Property<>();
//
//    public ObliFunction(Activity activity, BooleanProperty isFunctionScreenOpened) {
//        super(activity);
//        this.isFunctionScreenOpened = isFunctionScreenOpened;
//        fileExplorer = new FileExplorer(Arrays.asList(".csv", ".CSV"), MAIN_FOLDER, activity, header);
//
//        fileExplorer.getChosenFile().observe(new Observer<File>() {
//            @Override
//            public void observe(File oldValue, File newValue, Observation<File> observation) {
//
//                if (newValue == null) {
//                    ArrayList<ObliCircle> circles = new ArrayList<ObliCircle>();
//                    circles.add(new ObliCircle(80, 30, false, -50));
//                    circles.add(new ObliCircle(80, 60, true, -50));
//                    circles.add(new ObliCircle(80, 100, true, -50));
//                    currentMission.set(new OblimapperMission(circles));
//                } else {
//                    try {
//                        currentMission.set(OblimapperMission.getFromFile(newValue));
//                    } catch (IOException e) {
//                        fileExplorer.getChosenFile().set(null);
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).observeCurrentValue();
//    }
//
//    public Property<OblimapperMission> getCurrentMission() {
//        return currentMission;
//    }
//
//    @Override
//    public Drawable getFunctionDrawable() {
//        return ContextCompat.getDrawable(activity, R.drawable.btn_fmode_obli);
//    }
//
//    @Override
//    public void actionMenuButtonPressed() {
//        isFunctionScreenOpened.set(false);
//        isObliMenuOpened.set(!isObliMenuOpened.value());
//    }
//
//    public BooleanProperty isObliMenuOpened() {
//        return isObliMenuOpened;
//    }
//
//    public BooleanProperty isFunctionScreenOpened() {
//        return isFunctionScreenOpened;
//    }
//
//    public void showFileExplorer() {
//        fileExplorer.showDialog();
//    }
//
//    public Property<File> selectedFile() {
//        return fileExplorer.getChosenFile();
//    }
//
//    public FileExplorer getFileExplorer() {
//        return fileExplorer;
//    }
//
//    @Override
//    public SpecialFunctionType functionType() {
//        return SpecialFunctionType.OBLI;
//    }
//
//    public Removable addObliToMap(DroneController controller, final MapViewModel mapViewModel) {
//        ArrayList<Removable> removableList = new ArrayList<>();
//        final MapOblimapper oblimapper = new MapOblimapper(getCurrentMission());
//        mapViewModel.addMapItem(oblimapper);
//        removableList.add(new Removable() {
//            @Override
//            public void remove() {
//                mapViewModel.removeMapItem(oblimapper);
//            }
//        });
//        return new RemovableCollection(removableList);
//    }
//}