package eyesatop.util.android.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkStateReceiver extends BroadcastReceiver {

    private static ConnectivityChangeListener listener = null;

    public static void setListener(ConnectivityChangeListener newListener){
        listener = newListener;
    }

    public void onReceive(Context context, Intent intent) {

        if(listener != null){
            listener.onConnectivityChange(context,intent);
        }
    }

    public interface ConnectivityChangeListener {
        void onConnectivityChange(Context context,Intent intent);
    }
}
