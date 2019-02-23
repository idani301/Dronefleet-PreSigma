package eyesatop.util.android.dtm;


import android.app.Activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

public class TerrainAltitude implements DtmProvider {


    private final Property<Double> raiseValue = new Property<>(0D);

    Activity activity;

    String same_File = "";

    private byte[] myBit = new byte[2884802];

    List<DTMDataMagazine> magazine = new ArrayList<DTMDataMagazine>();

    public TerrainAltitude(Activity activity)
    {
        this.activity = activity;
    }

    private double getActualTerrainAltitude(double lat, double lon) {

        double myXX, myYY, restXX, restYY, fLon, fLat, alt_Point1, alt_Point2, alt_Point3, alt_Point4;
        double dX, dY;

        fLon = lon;
        fLat = lat;

        myXX = Math.floor(fLon);
        myYY = Math.floor(fLat);

        String searchWord;

        if (myYY >= 0)
        {
            searchWord = "N" + String.format("%02d", (int) myYY);
        }
        else
        {
            searchWord = "S" + String.format("%02d", (int) Math.abs(myYY));
        }

        if (myXX >= 0)
        {
            searchWord = searchWord + "E" + String.format("%03d", (int) myXX) + ".hgt";
        }
        else
        {
            searchWord = searchWord + "W" + String.format("%03d", (int) Math.abs(myXX)) + ".hgt";
        }

        searchWord = "DroneController_EyesOnTop_DTM" + "/" + searchWord;
        alt_Point1 = -99999;

        if (!same_File.equals(searchWord))
        {
            if (loadHGT(searchWord))
            {
                same_File = searchWord;
            }
            else
            {
                return alt_Point1;
            }
        }

        if (myXX >= 0)
        {
            restXX = (fLon - myXX);
        }
        else
        {
            restXX = (1 - (Math.abs(fLon) + 1 - Math.abs(myXX)));
        }

        if (myYY >= 0)
        {
            restYY = (fLat - myYY);
        }
        else
        {
            restYY = (1 - (Math.abs(fLat) + 1 - Math.abs(myYY)));
        }

        dX = (restXX * 1200) - Math.floor(restXX * 1200);
        restXX = Math.floor(restXX * 1200);

        dY = (restYY * 1200) - Math.floor(restYY * 1200);
        restYY = Math.floor(restYY * 1200);

        alt_Point1 = getAltFromByteArray(restYY, restXX * 2);

        alt_Point2 = getAltFromByteArray(restYY, restXX * 2 + 2);

        alt_Point3 = getAltFromByteArray(restYY + 1, restXX * 2);

        alt_Point4 = getAltFromByteArray(restYY + 1, restXX * 2 + 2);

        double a, b;

        if ((alt_Point1 == -99999) && (alt_Point2 != -99999))
        {
            b = alt_Point2;
        }
        else if ((alt_Point1 != -99999) && (alt_Point2 == -99999))
        {
            b = alt_Point1;
        }
        else if ((alt_Point1 == -99999) && (alt_Point2 == -99999))
        {
            b = -99999;
        }
        else
        {
            b = dX * (alt_Point2 - alt_Point1) + alt_Point1;
        }

        if ((alt_Point3 == -99999) && (alt_Point4 != -99999))
        {
            a = alt_Point4;
        }
        else if ((alt_Point3 != -99999) && (alt_Point4 == -99999))
        {
            a = alt_Point3;
        }
        else if ((alt_Point3 == -99999) && (alt_Point4 == -99999))
        {
            a = -99999;
        }
        else
        {
            a = dX * (alt_Point4 - alt_Point3) + alt_Point3;
        }

        if ((a == -99999) && (b == -99999))
        {
            return -99999;
        }
        else if ((a == -99999) && (b != -99999))
        {
            return b;
        }
        else if ((a != -99999) && (b == -99999))
        {
            return a;
        }
        else
        {
            return Math.round(dY * (a - b) + b);
        }
    }

    private double getTerrainAltitude(double lat, double lon) throws TerrainNotFoundException {
        double altitude = getActualTerrainAltitude(lat,lon);
        if(altitude == -99999){
            throw new TerrainNotFoundException();
        }

        return altitude + dtmRaiseValue().value();
    }

    byte[] readAllDataFromFile(String file) throws Exception
    {
        InputStream stream = activity.getAssets().open(file);
        // byte[] b = new byte[stream.available()];
        byte[] b = new byte[2884802]; // size of hgt file
        stream.read(b);
        stream.close();

        return b;
    }

    double getAltFromByteArray(double poinY, double poinX)
    {
        int pos = (1200 - (int) poinY) * 2402 + (int) poinX;
        byte a = myBit[pos];
        byte b = myBit[pos + 1];

        int num_a = a;
        if (num_a < 0) num_a += 256;
        int num_b = b;
        if (num_b < 0) num_b += 256;

        double alt = (short) (num_a * 256 + num_b);
        // double alt = (short) (myBit[pos] * 256 + myBit[pos + 1]);
        // double alt = (short) (myBit[pos] * 0x100 + myBit[pos + 1]);

        if (alt == -32768) alt = -99999;

        return alt;
    }

    private boolean loadHGT(String file)
    {
        for (int ii = 0; ii < magazine.size(); ii++)
        {
            if (magazine.get(ii).fileName.equals(file))
            {
//                for (int j = 0; j < myBit.length; j++)
//                    myBit[j] = magazine.get(ii).myBit[j];
                System.arraycopy(magazine.get(ii).myBit, 0, myBit, 0, magazine.get(ii).myBit.length);

                return true;
            }
        }

        // read file from memory
        try
        {
            myBit = readAllDataFromFile(file);

            if (magazine.size() == 5) magazine.remove(0);

            DTMDataMagazine data = new DTMDataMagazine();
            data.fileName = file;

//			for (int j = 0; j < myBit.length; j++)
//				data.myBit[j] = myBit[j];
            System.arraycopy(myBit, 0, data.myBit, 0, myBit.length);

            magazine.add(data);

            return true;
        } catch (Exception ex)
        {
            return false;
        }
    }

    @Override
    public ObservableValue<Double> dtmRaiseValue() {
        return raiseValue;
    }

    @Override
    public void raiseDTM(double value) {
        raiseValue.set(raiseValue.value() + value);
    }

    @Override
    public void lowerDTM(double value) {
        raiseValue.set(raiseValue.value() - value);
    }

    @Override
    public void clearRaiseValue() {
        raiseValue.set(0D);
    }

    @Override
    public DtmProvider duplicate() {
        return this;
    }

    @Override
    public double density() {
        return 20;
    }

    @Override
    public double terrainAltitude(Location location) throws TerrainNotFoundException {
        return getTerrainAltitude(location.getLatitude(),location.getLongitude());
    }

    @Override
    public double terrainAltitude(double lat, double lon) throws TerrainNotFoundException {
        return getTerrainAltitude(lat,lon);
    }

    @Override
    public double maxTerrainAltitudeInArea(Location location,double areaSquareSide) throws TerrainNotFoundException {
        return terrainAltitude(location);
    }

    @Override
    public List<Location> corners() {
        return null;
    }

    private static final double DTM_MAX_STEPS = 400;
    private static final double DTM_STEP_DISTANCE = 5;

    @Override
    public double maxSteps() {
        return DTM_MAX_STEPS;
    }

    @Override
    public double stepDistanceInMeters() {
        return DTM_STEP_DISTANCE;
    }
}
