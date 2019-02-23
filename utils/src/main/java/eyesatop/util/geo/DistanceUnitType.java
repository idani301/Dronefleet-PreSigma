package eyesatop.util.geo;

import java.text.DecimalFormat;

/**
 * Created by Idan on 12/12/2017.
 */

public enum DistanceUnitType {
    FEET,
    METER;

    private enum DistanceUnitMeasureType {
        METER("m"),
        KILOMETER("km"),
        FEET("ft"),
        MILE("mi");

        private final String name;

        DistanceUnitMeasureType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public double fromMeterToMe(double valueInMeters){
            switch (this){

                case METER:
                    return valueInMeters;
                case KILOMETER:
                    return valueInMeters/1000D;
                case FEET:
                    return valueInMeters/FEET_IN_METERS;
                case MILE:
                    return valueInMeters/MILE_IN_METERS;
                default:
                    return Double.NaN;
            }
        }
    }

    private static final double MILE_IN_METERS = 1609.34;
    private static final double FEET_IN_METERS = 0.3048;

    public static String formatNumber(DistanceUnitType distanceUnitType,int numOfDigitsAfterDot,double valueInMeters){

        DistanceUnitMeasureType measureType = DistanceUnitMeasureType.METER;

        switch (distanceUnitType){

            case FEET:

                if(valueInMeters >= MILE_IN_METERS){
                    measureType = DistanceUnitMeasureType.MILE;
                }
                else{
                    measureType = DistanceUnitMeasureType.FEET;
                }
                break;
            case METER:

                if(valueInMeters >= 1000){
                    measureType = DistanceUnitMeasureType.KILOMETER;
                }
                else{
                    measureType = DistanceUnitMeasureType.METER;
                }
        }
        return formatNumberOnly(numOfDigitsAfterDot,measureType.fromMeterToMe(valueInMeters)) + measureType.getName();
    }

    private static String formatNumberOnly(int numOfDigitsAfterPeriod,double value){

        if(value == 0){
            return "0";
        }

        String decimalFormatString = "#.";
        for(int i=0; i<numOfDigitsAfterPeriod; i++){
            decimalFormatString+="0";
        }

        if(Math.abs(value) < 0.99D){
            return "0" + new DecimalFormat(decimalFormatString).format(value);
        }

        return new DecimalFormat(decimalFormatString).format(value);
    }
}
