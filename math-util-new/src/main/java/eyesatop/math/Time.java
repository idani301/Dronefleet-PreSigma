package eyesatop.math;

/**
 * Created by Einav on 20/06/2017.
 */

public class Time {

    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;
    private final double second;
    private final int TimeZone; //summer clock add one hour to time zone
    private final boolean summerClock;


    public Time(int year, int month, int day, int hour, int minute, double second, int timeZone, boolean summerClock) {
        if (second > 60){
            double temp = second%60;
            minute += (int)(second-temp)/60;
            second = temp;
        }
        if (minute >= 60){
            int temp = minute%60;
            hour += (minute-temp)/60;
            minute = temp;
        }
        if (hour >= 24){
            int temp = hour%24;
            day += (hour-temp)/24;
            hour = temp;
        }
        int numDays = 0;
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                numDays = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                numDays = 30;
                break;
            case 2:
                if (((year % 4 == 0) &&
                        !(year % 100 == 0))
                        || (year % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
        }
        if (day > numDays){
            int temp = day%numDays;
            month += (day-temp)/numDays;
            day = temp;
        }
        if (month > 12){
            int temp = month%12;
            year += (month-temp)/12;
            month = temp;
        }
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        TimeZone = timeZone;
        this.summerClock = summerClock;
    }


    @Override
    public String toString() {
        return "Time{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", TimeZone=" + TimeZone +
                ", summerClock=" + summerClock +
                '}';
    }
}
