package org.mrcpp.other;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
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
    public static final String DETAIL_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss";
    public static final String DIR_CSV_MATADAUN = "/MATADAUN/CSV/";
    public static final String DIR_IMG_MATADAUN = "/MATADAUN/IMG/";

    public String EHCapitalize(final String line) {
        String sRep = line.replace("_", " ");
        return Character.toUpperCase(sRep.charAt(0)) + sRep.substring(1);
    }

    public String getCurrentTimeStamp(String type) {
        try {
            if(type.equals("simple")) {
                SimpleDateFormat sdf = new SimpleDateFormat(EHHelper.SIMPLE_DATE_FORMAT);
                return sdf.format(new Date());
            } else if(type.equals("detail")) {
                SimpleDateFormat sdf = new SimpleDateFormat(EHHelper.DETAIL_DATE_FORMAT);
                return sdf.format(new Date());
            } else {
                //simple date for default
                SimpleDateFormat sdf = new SimpleDateFormat(EHHelper.SIMPLE_DATE_FORMAT);
                return sdf.format(new Date());
            }
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
                        + getCurrentTimeStamp("simple") + ","
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

    public void writeToCSV2 (String secv, String sleaftype, String sspad, String snitro, Double valLat, Double valLon, String sketerangan, String snamefile, String pathroot, Context ccontext) {
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
                        + getCurrentTimeStamp("detail") + ","
                        + secv + ","
                        + sspad+ ","
                        + snitro + ","
                        + valLon + ","
                        + valLat + ","
                        + sketerangan
                        + System.getProperty("line.separator"));
                fwWriter.flush();
                fwWriter.close();
                MediaScannerConnection.scanFile(ccontext, new String[]{Environment.getExternalStorageDirectory().toString()}, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Uri saveImageToExternalStorage(Bitmap img, String imgtypename, Context context) {
        Uri u;
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MATADAUN/ECV/IMG/";

        StringBuilder sb = new StringBuilder();
        sb.append(imgtypename + "_" + getCurrentTimeStamp("detail") + ".png");
        String sNameFileImg = sb.toString();
        sNameFileImg.replace(" ", "_");

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, sNameFileImg);
        cv.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        try {
            File dir = new File(fullPath);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = context.getContentResolver().openOutputStream(uri);

            //OutputStream fOut = null;
            File file = new File(fullPath, sNameFileImg);

            if(file.exists()) {
                file.delete();
            }

            file.createNewFile();
            u = Uri.fromFile(file);

            fOut = new FileOutputStream(file);
            // 100 means no compresion, the lower you go, the stronger the compresion
            img.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            fOut.flush();
            fOut.close();
            return u;
        } catch (Exception e) {
            Log.e("evan save image", e.getMessage());
            return null;
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
