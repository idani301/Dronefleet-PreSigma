package eyesatop.util.android.wifimanager;
import android.content.*;
import android.net.wifi.*;
import android.telephony.TelephonyManager;

import java.lang.reflect.*;

public class WifiManagerEyesatop {

    //check whether wifi hotspot on or off
    public static boolean isHotSpotOn(Context context) {
        android.net.wifi.WifiManager wifimanager = (android.net.wifi.WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    // toggle wifi hotspot on or off
    public static void setHotSpotEnabled(Context context,boolean enable) {
        android.net.wifi.WifiManager wifimanager = (android.net.wifi.WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            if(enable){
                if(!isHotSpotOn(context)){
                    Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    method.invoke(wifimanager, wificonfiguration, !isHotSpotOn(context));
                }
            }
            else{
                if(isHotSpotOn(context)){
                    wifimanager.setWifiEnabled(false);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSimCardInserted(Context context){

        TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        return simState == TelephonyManager.SIM_STATE_READY;
    }
} // end of class
