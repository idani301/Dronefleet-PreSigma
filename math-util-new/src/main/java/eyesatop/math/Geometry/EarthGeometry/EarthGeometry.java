package eyesatop.math.Geometry.EarthGeometry;

import java.util.HashMap;

import eyesatop.math.Geometry.Ellipsoid;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 29/06/2017.
 */


public class EarthGeometry {

    private HashMap<DatumName,Datum> datum = new HashMap<>();

    public EarthGeometry(){
        for(DatumName tempDatum : DatumName.values()){
            switch (tempDatum){

                case WGS84:
                    datum.put(tempDatum, new Datum(Ellipsoid.datumEllipse(Point3D.zero(),6378137,0.00669438), 0.9996, Point2D.cartesianPoint(500000,0)));
                    break;
                case ED50:
                    datum.put(tempDatum, new Datum(Ellipsoid.datumEllipse(Point3D.cartesianPoint(103,106,141),6378388,0.00672267),1.04,Point2D.cartesianPoint(500000,0)));
                    break;
                case ED50I:
                    datum.put(tempDatum, new Datum(Ellipsoid.FlatteningEllipse(Point3D.cartesianPoint(-134,-48,149),6378160,1.0/298.25),0.99959,Point2D.cartesianPoint(500000 - 0.09,-2.43)));
                    break;
                case GRS80:
                    datum.put(tempDatum, new Datum(new Ellipsoid(Point3D.cartesianPoint(48,55,52),6378137.0,6356752.3141),1.0000067,Point2D.cartesianPoint(219529.584,2885516.9488)));
                    break;
                case ITM:
                    datum.put(tempDatum, new Datum(new Ellipsoid(Point3D.cartesianPoint(-48,55,52),6378137.0,6356752.3141),1.0000067,Point2D.cartesianPoint(219529.584,626907.39)));
            }
        }
    }


    public Ellipsoid getEllipsoidDatum(DatumName datum){
        return this.datum.get(datum).getEarthEllipsoid();
    }

    public double getK0(DatumName datum) {
        return this.datum.get(datum).getScale();
    }

    public Point2D getFalsePoint(DatumName datum){
        return this.datum.get(datum).getFalse_Point();
    }

    public void insertNewParameterToDatum(DatumName datum, Point3D newCenter){
        Ellipsoid ellipsoid = new Ellipsoid(newCenter,getEllipsoidDatum(datum).getA(),getEllipsoidDatum(datum).getB());
        Datum datum1 = new Datum(getEllipsoidDatum(datum),getK0(datum),getFalsePoint(datum));
        this.datum.replace(datum,datum1);
    }

    public void insertNewParameterToDatum(DatumName datum, double scale){
        Ellipsoid ellipsoid = getEllipsoidDatum(datum);
        Datum datum1 = new Datum(getEllipsoidDatum(datum),scale,getFalsePoint(datum));
        this.datum.replace(datum,datum1);
    }

}
