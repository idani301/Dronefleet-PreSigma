package eyesatop.apps.remote;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class RemoteAppApplication extends Application {

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        MultiDex.install(this);
        com.secneo.sdk.Helper.install(this);
    }
}
