package eyesatop.eyesatop.util.readfromtxtfile;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Time;

/**
 * Created by Einav on 20/06/2017.
 */

public class ReadHeaders {

    public static long ReadCameraSN(File file) throws Exception {
        Pattern pattern = Pattern.compile("(\\d{10})");
        Matcher matcher = pattern.matcher(file.getName());
        if(matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new Exception("Wrong file to match camera serial number");
    }

    public static Time ReadCameraTime(File file) throws Exception {
        Pattern pattern = Pattern.compile("(\\d*)_(\\d*)_(\\d{4})_(\\d*)_(\\d*)_(\\d*)_(True|False)");
        Matcher matcher = pattern.matcher(file.getName());
        if(matcher.find()){
            int year = Integer.parseInt(matcher.group(3));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(1));
            int hour = Integer.parseInt(matcher.group(4));
            int minute = Integer.parseInt(matcher.group(5));
            double second = Double.parseDouble(matcher.group(6));
            boolean summerClock = Boolean.parseBoolean(matcher.group(7));

            return new Time(year,month,day,hour,minute,second,2,summerClock);

        }
        throw new Exception("Fail to match time from header");
    }

    public static Location ReadCameraLocation(File file) throws Exception {
        Pattern pattern = Pattern.compile("GPS_([\\d]+[.]+[\\d]+)_([\\d]+[.]+[\\d]+)");
        Matcher matcher = pattern.matcher(file.getName());
        if(matcher.find()){
            double latitude = Double.parseDouble(matcher.group(1));
            double longitude = Double.parseDouble(matcher.group(2));


            return new Location(latitude,longitude);

        }
        throw new Exception("Fail to match time from header");
    }
}
