package eyesatop.pipeapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class PipeAppApplication extends Application {

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        MultiDex.install(this);
        com.secneo.sdk.Helper.install(this);
    }
}
