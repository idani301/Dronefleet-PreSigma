package eyesatop.ui_generic.viewmodels;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import eyesatop.ui_generic.R;
import eyesatop.util.android.HandlerExecutor;
import eyesatop.util.android.display.DisplayEyesatop;
import eyesatop.util.android.files.EyesatopAppsFilesUtils;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;
import logs.LoggerTypes;

public abstract class EyesatopAppsActivity extends Activity {

    public enum ApplicationType {
        REAL("Real"),
        SIMULATOR("Simulator");

        private final String name;

        ApplicationType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    ArrayList<String> permissionsNeedsRequest = new ArrayList<>();
    ArrayList<String> permissionsGranted = new ArrayList<>();
    ArrayList<String> missingPermissions = new ArrayList<>();

    private final CountDownLatch permissionsLatch = new CountDownLatch(1);
    private TextViewModel loadingText;
    private ImageViewModel loadingImage;
    private TextViewModel versionText;
    private SpinnerViewModel spinner;
    private ViewModel appTypeView;
    private final Property<String> networkInfo = new Property<>();

    //    private EditTextViewModel editText;
    private TextViewModel doneText;

    protected EyesatopAppsActivity() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length != permissions.length){
            throw new IllegalArgumentException("Mismatch in the size of the grant permissions and requested permissions.");
        }

        for(int i=0; i < permissions.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                permissionsGranted.add(permissions[i]);
            }
        }

        for(String permission : permissionsRequired()){
            if(!permissionsGranted.contains(permission)){
                missingPermissions.add(permission);
            }
        }

        permissionsLatch.countDown();
    }

    @Override
    public void onBackPressed() {
        minimizeApp();
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void initLoggers(){
        List<LoggerTypes> logsAppRequire = logsRequired();
        if(logsAppRequire != null && logsAppRequire.size() > 0) {
            MainLogger.initInstance(EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.APP_LOGS,false),this.getClass().getSimpleName() + " Logs", logsAppRequire);
        }
    }


    protected abstract List<LoggerTypes>    logsRequired();

//    protected abstract boolean isLicenseRequired();

    protected @LayoutRes
    int loadingPageLayout(){
        return R.layout.loading_page_layout;
    }

    protected @IdRes
    int loadingText(){
        return R.id.loadingText;
    }

    protected  @IdRes int loadingImage(){
        return R.id.loadingImage;
    }

    protected Drawable loadingImageDrawable(){
        return ContextCompat.getDrawable(this,R.drawable.logo);
    }

    protected  TextViewModel getVersionText(){
        return versionText;
    }

    protected TextViewModel getLoadingText(){
        return loadingText;
    }

//    public EditTextViewModel getEditText() {
//        return editText;
//    }


    public ViewModel getAppTypeView() {
        return appTypeView;
    }

    public SpinnerViewModel getSpinner() {
        return spinner;
    }

    public TextViewModel getDoneText() {
        return doneText;
    }

    private static boolean everStarted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if(everStarted){
            return;
        }
        everStarted = true;

        super.onCreate(savedInstanceState);

        setContentView(loadingPageLayout());

        appTypeView = new ViewModel(findViewById(R.id.appTypeContainer));
        spinner = new SpinnerViewModel((Spinner) findViewById(R.id.applicationTypeSpinner),ApplicationType.values());
        loadingText = new TextViewModel((TextView) findViewById(loadingText()));
        loadingImage = new ImageViewModel((ImageView)findViewById(loadingImage()));
        loadingImage.imageDrawable().set(loadingImageDrawable());

//        editText = new EditTextViewModel((EditText) findViewById(R.id.loadingScreenEditText));
        doneText = new TextViewModel((TextView) findViewById(R.id.loadingScreenDoneText));

        versionText = new TextViewModel((TextView) findViewById(R.id.loadingScreenVersionText));
        versionText.text().set(this.getClass().getSimpleName());

        final Activity thisActivity = this;

        Thread welcomeThread = new Thread(new Runnable() {

            @Override
            public void run() {

                requestPermissions(thisActivity,permissionsRequired());

                try {
                    permissionsLatch.await();

                    if(missingPermissions.size() > 0) {
                        String permissionsErrorString = "Missing Permissions : " + "\n";

                        permissionsErrorString += missingPermissions.get(0);

                        for (int i = 1; i < missingPermissions.size(); i++) {
                            permissionsErrorString += "\n" + missingPermissions.get(i);
                        }
                        loadingText.text().set(permissionsErrorString);
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                if(isLicenseRequired()) {
//                    try {
//                        LicenseCheck.checkPermissions(thisActivity);
//                    } catch (DroneTaskException e) {
//                        e.printStackTrace();
//                        loadingText.text().set("License Fail : " + e.getErrorString());
//                        return;
//                    }
//                }
                DisplayEyesatop.init(thisActivity);

                Executor startingExecutor = Executors.newSingleThreadExecutor();

                startingExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        EyesatopAppsFilesUtils.getInstance().setApplicationName(thisActivity.getClass().getSimpleName());
                        initLogs(thisActivity);

                        postChecks(thisActivity);
                    }
                });
            }
        });
        welcomeThread.start();
    }
    protected abstract void postChecks(Activity thisActivity);

    private void requestPermissions(final Activity thisActivity, final List<String> permissionsRequired){

        if(permissionsRequired == null){
            permissionsLatch.countDown();
            return;
        }

        HandlerExecutor.MAIN_LOOPER_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                permissionsNeedsRequest = new ArrayList<String>();

                for(String permission : permissionsRequired){
                    if(ContextCompat.checkSelfPermission(thisActivity,
                            permission)
                            != PackageManager.PERMISSION_GRANTED){
                        permissionsNeedsRequest.add(permission);
                    }
                    else{
                        permissionsGranted.add(permission);
                    }
                }

                if(permissionsNeedsRequest.size() > 0) {

                    String[] permissionsNeedRequestArray = new String[permissionsNeedsRequest.size()];
                    for(int i=0; i<permissionsNeedRequestArray.length; i++){
                        permissionsNeedRequestArray[i] = permissionsNeedsRequest.get(i);
                    }

                    ActivityCompat.requestPermissions(thisActivity,
                            permissionsNeedRequestArray,
                            1);
                }
                else{
                    permissionsLatch.countDown();
                }
            }
        });
    }

    private void initLogs(Activity thisActivity){

        boolean hasLogs;
        try{
            initLoggers();
            hasLogs = true;
        }
        catch (Exception e){
            hasLogs = false;
        }

        if(hasLogs){

            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    MainLogger.logger.writeError(LoggerTypes.ERROR, throwable);

                    if(!throwable.getMessage().matches("(?i).*sql.*") && !throwable.getMessage().matches("(?i).*database.*")) {
                        MainLogger.logger.write_message_now(LoggerTypes.ERROR,"Exiting");
                        System.exit(1);
                    }
                    else{
                        MainLogger.logger.write_message_now(LoggerTypes.ERROR,"not exiting");
                    }
                }
            });
            MainLogger.logger.write_message_now(LoggerTypes.SDK_INIT,"After init logs");
            return;
        }

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("We will be running this run without logs.")
                .setTitle("Logs Missing");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected ObservableValue<String> getNetworkInfo() {
        return networkInfo;
    }

    public abstract List<String> permissionsRequired();
}