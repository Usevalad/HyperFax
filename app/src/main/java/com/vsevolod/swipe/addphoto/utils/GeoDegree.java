package com.vsevolod.swipe.addphoto.utils;

/**
 * Created by vsevolod on 20.06.17.
 * This class converting exifInterface data to a valid one
 */

import android.media.ExifInterface;

import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GeoDegree {
    private boolean valid = false;
    private Float Latitude, Longitude;

    public GeoDegree(String photoUri) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(photoUri);

            String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if ((attrLATITUDE != null)
                    && (attrLATITUDE_REF != null)
                    && (attrLONGITUDE != null)
                    && (attrLONGITUDE_REF != null)) {
                valid = true;

                if (attrLATITUDE_REF.equals("N")) {
                    Latitude = convertToDegree(attrLATITUDE);
                } else {
                    Latitude = 0 - convertToDegree(attrLATITUDE);
                }

                if (attrLONGITUDE_REF.equals("E")) {
                    Longitude = convertToDegree(attrLONGITUDE);
                } else {
                    Longitude = 0 - convertToDegree(attrLONGITUDE);
                }
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }


    private Float convertToDegree(String stringDMS) {
        Float result;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double FloatS = S0 / S1;

        result = (float) (FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return (String.valueOf(Latitude)
                + ", "
                + String.valueOf(Longitude));
    }

    public double getLatitude() {
        return round(Latitude);
    }

    public double getLongitude() {
        return round(Longitude);
    }

    /*
           rounding double to 6 numbers after dot
     */
    private double round(double d) {
        BigDecimal bd = new BigDecimal(d).setScale(6, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}