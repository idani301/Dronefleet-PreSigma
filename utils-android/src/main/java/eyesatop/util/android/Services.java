//package eyesatop.util.android;
//
//import android.app.ActivityManager;
//import android.app.ActivityManager.RunningServiceInfo;
//import android.content.Context;
//
//import java.util.List;
//
//public class Services {
//
//    public static boolean isRunning(Class serviceClass, Context appContext) {
//        ActivityManager activityManager = (ActivityManager) appContext.getSystemService(
//                Context.ACTIVITY_SERVICE);
//        List<RunningServiceInfo> services =
//                activityManager.getRunningServices(Integer.MAX_VALUE);
//
//        for (RunningServiceInfo runningServiceInfo : services) {
//            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())){
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//}
