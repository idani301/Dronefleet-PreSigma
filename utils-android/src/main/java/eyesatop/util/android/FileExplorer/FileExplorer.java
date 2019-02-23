package eyesatop.util.android.FileExplorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 08/10/2017.
 */

public class FileExplorer {
    private final List<String> fileTypesToShow;
    private final Property<File> chosenFile = new Property<>();
    private final ObservableList<String> currentFileHierarchy = new ObservableList<>();
    private final Activity activity;

    private final String header;

    private boolean hideNavigationButtons = false;
    private boolean hideRefreshButton = false;

    private final File directory;

    public FileExplorer(List<String> fileTypesToShow, File directory, Activity activity, String header) {

        this.fileTypesToShow = fileTypesToShow;
        this.activity = activity;
        this.directory = directory;
        this.header = header;

        resetHierarchy();
    }

    public FileExplorer(File directory, Activity activity, String header) {
        this(null,directory,activity, header);
    }

    public void showDialog(){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final String[] files = getCurrentFolderFiles();
        final File currentFolder = getCurrentFolder();
        builder.setTitle(header + "\nCurrent Path : " + currentFolder.getAbsolutePath());

        builder.setItems(files, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                File selectedFile = new File(currentFolder.getAbsolutePath() + "/" + files[which]);

                if(!selectedFile.exists()){
                    showDialog();
                }
                else if(selectedFile.isDirectory()){
                    currentFileHierarchy.add(files[which]);
                    showDialog();
                }
                else{
                    chosenFile.set(selectedFile);
                }
//                dialog.dismiss();
            }
        });

        if(!hideRefreshButton) {
            builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showDialog();
                }
            });
        }


        if(!hideNavigationButtons) {
            builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetHierarchy();
                    showDialog();
                }
            });
        }

        if(currentFileHierarchy.size() > 0 && !hideNavigationButtons) {
            builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (currentFileHierarchy.size() > 0) {
                            currentFileHierarchy.remove(currentFileHierarchy.size() - 1);
                            showDialog();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
//                    dialog.dismiss();
                }
            });
        }

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private File getCurrentFolder(){
        String finalFolderString = "";

        for (String str : currentFileHierarchy) {
            finalFolderString += "/" + str;
        }

        return new File(finalFolderString);
    }

    private String[] getCurrentFolderFiles(){

        File currentFolder = getCurrentFolder();

        if(!currentFolder.exists()){
            resetHierarchy();
            currentFolder = getCurrentFolder();
        }

        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                File sel = new File(dir, filename);

                if(sel.isDirectory()){
                    return true;
                }

                if(fileTypesToShow != null){
                    for(String fileType : fileTypesToShow){
                        if(filename.contains(fileType)){
                            return true;
                        }
                    }
                }
                else{
                    return true;
                }

                return false;
            }

        };
        return currentFolder.list(filter);
    }

    private void resetHierarchy(){
        currentFileHierarchy.clear();

        File directoryRequested = directory;
        if(!directoryRequested.exists()){
            if(!directoryRequested.mkdirs()){
                return;
            }
        }

        if(directory != null) {
            String[] fileHierarchy = directory.getAbsolutePath().split("/");
            for(String str : fileHierarchy){
                if(!str.equals("")){
                    currentFileHierarchy.add(str);
                }
            }
        }
    }

    public void setHideNavigationButtons(boolean hideNavigationButtons) {
        this.hideNavigationButtons = hideNavigationButtons;
    }

    public void setHideRefreshButton(boolean hideRefreshButton) {
        this.hideRefreshButton = hideRefreshButton;
    }

    public Property<File> getChosenFile() {

        return chosenFile;
    }
}
