package org.mrcpp.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EHHElper class made by evanhutomo for general purpose
 *
 * */

public class EHHelper {
    public static final String[] LEAF_TYPE = new String[] {
            "Soybean",
            "Rice",
            "Corn",
            "Cassava",
            "Ground nut",
            "Yam"
    };

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DETAIL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DIR_CSV_MATADAUN = "/MATADAUN/CSV/";
    public static final String DIR_IMG_MATADAUN = "/MATADAUN/IMG/";

    public String EHCapitalize(final String line) {
        String sRep = line.replace("_", " ");
        return Character.toUpperCase(sRep.charAt(0)) + sRep.substring(1);
    }

    public String getCurrentTimeStamp() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(EHHelper.SIMPLE_DATE_FORMAT);
            return sdf.format(new Date());
        } catch (Exception e) {
            return "1900-00-00";
        }
    }
    /**
     * method <b>writeToCSV1</b> use to write a line of Chlorophyll and Nitrogen value to .csv file
     * @author evanhutomo
     * @version 1.0
     * @param valChlorophyll fill with chlorophyll value
     * @param valNitrogen fill with nitrogen value
     * @param typeleaf type leaf
     * @param snamefile named your .csv file
     * @param pathroot set .csv root folder
     */
    public void writeToCSV1 (String valChlorophyll, String valNitrogen, Double valLat, Double valLon, String typeleaf, String snamefile, String pathroot, Context ccontext) {
        if(typeleaf != null ) {
            String sNameLeaf = EHCapitalize(typeleaf);

            //save to MATADAUN/CSV/ directory
            File fileMD = new File(Environment.getExternalStorageDirectory(), pathroot + snamefile);

            try {
                FileWriter fwWriter = new FileWriter(fileMD, true);
                fwWriter.write("" + sNameLeaf + ","
                        + getCurrentTimeStamp() + ","
                        + valChlorophyll + ","
                        + valNitrogen + ","
                        + valLat + ","
                        + valLon
                        + System.getProperty("line.separator"));
                fwWriter.flush();
                fwWriter.close();
                MediaScannerConnection.scanFile(ccontext, new String[]{Environment.getExternalStorageDirectory().toString()}, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void writeToCSV2 (String secv, String sleaftype, String sspad, String snitro, Double valLat, Double valLon, String snamefile, String pathroot, Context ccontext) {
        if(sleaftype!= null ) {
            String sNameLeaf = EHCapitalize(sleaftype);

            if(valLat == null || valLon == null) {
                valLat = 0.0;
                valLon = 0.0;
            }
            //save to MATADAUN/ECV/ directory
            File fileMD = new File(Environment.getExternalStorageDirectory(), pathroot + snamefile);

            try {
                FileWriter fwWriter = new FileWriter(fileMD, true);
                fwWriter.write("" + sNameLeaf + ","
                        + getCurrentTimeStamp() + ","
                        + secv + ","
                        + sspad+ ","
                        + snitro + ","
                        + valLon + ","
                        + valLat
                        + System.getProperty("line.separator"));
                fwWriter.flush();
                fwWriter.close();
                MediaScannerConnection.scanFile(ccontext, new String[]{Environment.getExternalStorageDirectory().toString()}, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void showGPSDisabledAlertToUser(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog adGPS = alertDialogBuilder.create();
        adGPS.show();
    }
}
