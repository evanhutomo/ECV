package org.mrcpp.ecv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.mrcpp.constant.EHConstant;
import org.mrcpp.other.EHHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TempResultActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public TempResultActivity() {
        ehLeafTitle = new EHConstant();
        ehHelper = new EHHelper();
    }


    Spinner spnLeafType;
    EditText etSPAD;
    EditText etNitro;
    EditText etKeterangan;
    ImageView imgview;

    TextView tvResultECV, tvPxlGreen, tvPxlWhite, tvProgress;
    String[] arrData4Metadata = new String[8];

    EHConstant ehLeafTitle;

    String sleaftype, sspad, snitro, secv, sketerangan;
    Double CV;

    // EHHElper ivar
    EHHelper ehHelper;
    String sNameFile;

    // Google API ivar
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView tvLat, tvLong;
    Double dLat, dLon;

    // bitmap
    Bitmap bmp, bmpResized;
    double iImgBroadPixel;
    String mCurrentPhotoPath;

    File photoFile = null;
    Button btnSave, btnMetadata;

    //progressbar
    private ProgressBar progressBar;

    //Compression constanta
    private static final Double KONSTANTA_IMG = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_result);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //google sync lat long
        tvLat = (TextView) findViewById(R.id.tvLatResult);
        tvLong = (TextView) findViewById(R.id.tvLongResult);

        buildGoogleApiClient();
        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
        }
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setText("WAIT..");
        btnSave.setEnabled(false);
//        btnSave.setBackgroundColor(Color.LTGRAY);

        btnMetadata = (Button) findViewById(R.id.btnMetadata);
        btnMetadata.setVisibility(View.GONE);
        btnMetadata.setEnabled(false);

        spnLeafType = (Spinner) findViewById(R.id.spnLeaftype);
        etSPAD = (EditText) findViewById(R.id.etSPAD);
        etNitro = (EditText) findViewById(R.id.etNitro);
        etKeterangan = (EditText) findViewById(R.id.etKeterangan);
        imgview = (ImageView) findViewById(R.id.imgLeafTemp);
        tvResultECV = (TextView) findViewById(R.id.tvResultECV);
        tvPxlGreen = (TextView) findViewById(R.id.tvPxlGreenResult);
        tvPxlWhite = (TextView) findViewById(R.id.tvPxlWhiteResult);

        tvProgress = (TextView) findViewById(R.id.tvProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.getProgressDrawable().setColorFilter(0xff00be8c, android.graphics.PorterDuff.Mode.SRC_ATOP);

        dispatchTakePictureIntent();
        //Intent iCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(iCamera, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        erasedTempFile();
    }


    private File createImageFile() throws IOException {
        //create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_mm_dd_HHmmss").format(new Date());
        String imageFilename = "ecvtemp_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFilename,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_TAKE_PHOTO);
    }

//    public String getPath(Uri uri) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //ensure that there's camera activity to handle the intent

        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch(IOException io) {

            }

            //continue
            if(photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void erasedTempFile() {
        if(photoFile.exists()) {
            Log.i("evan deleted", "exist");
            photoFile.delete();
        } else {
            Log.i("evan deleted", "file didnt exist");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if(photoFile.exists()) {
                //recycle after use
                bmpResized = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                bmp = Bitmap.createScaledBitmap(bmpResized, (int) (bmpResized.getWidth() * KONSTANTA_IMG), (int) (bmpResized.getHeight() * KONSTANTA_IMG), true);
                if(bmpResized !=bmp) {
                    bmpResized.recycle();
                }

                iImgBroadPixel = ((int) (bmpResized.getWidth() * KONSTANTA_IMG) * (int) (bmpResized.getHeight() * KONSTANTA_IMG));
                progressBar.setMax((int) iImgBroadPixel);
                imgview.setImageBitmap(bmp);
                new CountCV().execute("");
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            erasedTempFile();
            if(!photoFile.exists()) {
                Log.i("evan deleted", "file already deleted");
                Intent menu = new Intent(TempResultActivity.this, MainActivity.class);
                startActivity(menu);
                TempResultActivity.this.finish();
            }
        }
    }

    int jumlahHijau = 0;
    int jumlahPutih = 0;

    private class CountCV extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            double AvRs=0,AvGs=0,AvBs=0,AvRr=0,AvGr=0,AvBr=0,Er=0,Eg=0,Eb=0,Rfix=0,Gfix=0,Bfix=0;

            int Rs=0,Gs=0,Bs=0,Rr=0,Gr=0,Br=0, counter = 0;
            String sCond = "normal";

            for(int i = 0;i<bmp.getWidth();i++){
                for(int j = 0;j<bmp.getHeight();j++){
                    int r = Color.red(bmp.getPixel(i, j));
                    int g = Color.green(bmp.getPixel(i, j));
                    int b = Color.blue(bmp.getPixel(i, j));
                    if(((r>8)&&(r<58)) && ((g>16)&&(g<86)) && ((b>3)&&(b<34))){
                        Rs += r;
                        Gs += g;
                        Bs += b;
                        jumlahHijau++;
                    }else if (((r>149)&&(r<226)) && ((g>149)&&(g<226)) && ((b>149)&&(b<226))) {
                        Rr += r;
                        Gr += g;
                        Br += b;
                        jumlahPutih++;
                    }
                    counter++;
                }
                counter++;
                publishProgress(counter);
            }
            Log.i("evan jumlahhijau", String.valueOf(jumlahHijau));
            Log.i("evan jumlahputih", String.valueOf(jumlahPutih));

            iImgBroadPixel = jumlahHijau+jumlahPutih;
            double dAreaPix = (double) (jumlahHijau/iImgBroadPixel) * 100;

            if(dAreaPix < 5.0) {
                sCond = "err";
            } else if(dAreaPix >= 5.0 && dAreaPix <= 100.0) {
                AvRs = Rs / (jumlahHijau-1);
                AvGs = Gs / (jumlahHijau-1);
                AvBs = Bs / (jumlahHijau-1);

                AvRr = Rr / (jumlahPutih-1);
                AvGr = Gr / (jumlahPutih-1);
                AvBr = Br / (jumlahPutih-1);

                Er = 255-AvRr ;
                Eg = 255-AvGr ;
                Eb = 255-AvBr ;

                Rfix = AvRs + Er;
                Gfix = AvGs + Eg;
                Bfix = AvBs + Eb;

                CV = ((Rfix+Gfix+Bfix)/255)*100/3;
                secv = String.valueOf(CV);

                arrData4Metadata[0] = String.valueOf(CV);
                arrData4Metadata[1] = String.valueOf(dLat);
                arrData4Metadata[2] = String.valueOf(dLon);
                arrData4Metadata[3] = String.valueOf(jumlahHijau);
                arrData4Metadata[4] = String.valueOf(jumlahPutih);
                arrData4Metadata[5] = String.valueOf(iImgBroadPixel);

            }


            if(sCond.equals("normal")) {
                sCond = secv;
            } else {
                sCond = "err";
            }
            return sCond;
        }

        @Override
        protected void onPostExecute(String s) {
            if(!s.equals("err")) {
                tvResultECV.setText(s);
                tvPxlGreen.setText(String.valueOf(jumlahHijau));
                tvPxlWhite.setText(String.valueOf(jumlahPutih));

                btnSave.setText("SAVE");
                btnSave.setBackgroundColor(Color.parseColor("#22AF87"));
                btnSave.setEnabled(true);

                tvProgress.setText("done!");
                onSaveTheData();
                onSeeMetadata();
            } else {
                AlertDialog.Builder abCSVNull = new AlertDialog.Builder(TempResultActivity.this);
                abCSVNull.setMessage("Object is invalid, please take image again.");
                abCSVNull.setCancelable(true);
                abCSVNull.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        erasedTempFile();
                        TempResultActivity.this.finish();
                    }
                });

                AlertDialog ad = abCSVNull.create();
                ad.show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            tvProgress.setText("calculating ECV..");
            progressBar.setProgress(values[0]);
        }
    }

    public Boolean verifyData() {
        String sErr;

        sleaftype = String.valueOf(spnLeafType.getSelectedItem());
        sspad = String.valueOf(etSPAD.getText());
        snitro = String.valueOf(etNitro.getText());
        sketerangan = String.valueOf(etKeterangan.getText());
        if(sketerangan.equals("")) {
            sketerangan = "-";
        }
        if(sleaftype.equals("...")) {
            getErr("Leaf type");
            return false;
        } else if(sspad.equals("") || sspad.isEmpty()) {
            getErr("SPAD");
            return false;
        } else if (snitro.equals("") || snitro.isEmpty()) {
            getErr("Nitrogen");
            return false;
        } else {
            return true;
        }

    }

    public void getErr(String paramErr) {
        AlertDialog.Builder abNull = new AlertDialog.Builder(this);
        abNull.setMessage("Please fill the " + paramErr + " field properly");
        abNull.setCancelable(true);
        AlertDialog ad = abNull.create();
        ad.show();
    }

    private void setAllWidgetDisabled(Boolean param) {
        if(param) {
            btnSave.setText("SAVED!");
            btnSave.setClickable(false);
            btnSave.setBackgroundColor(Color.LTGRAY);

            etNitro.setEnabled(false);
            etSPAD.setEnabled(false);
            etKeterangan.setEnabled(false);
            spnLeafType.setEnabled(false);

            tvResultECV.setTextColor(Color.LTGRAY);
            tvLat.setTextColor(Color.LTGRAY);
            tvLong.setTextColor(Color.LTGRAY);
            tvProgress.setTextColor(Color.LTGRAY);
            tvPxlGreen.setTextColor(Color.LTGRAY);
            tvPxlWhite.setTextColor(Color.LTGRAY);

            progressBar.getProgressDrawable().setColorFilter(0xffffffff, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

    }
    public void onSeeMetadata() {
        btnMetadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSeeMetadata = new Intent(TempResultActivity.this, MetadataActivity.class);
                iSeeMetadata.putExtra("metadata", arrData4Metadata);
                startActivity(iSeeMetadata);
            }
        });
    }

    Uri uriImg;
    String sImgPath;
    public void onSaveTheData() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyData()) {

                    //save image
                    uriImg = ehHelper.saveImageToExternalStorage(bmp, sleaftype, TempResultActivity.this);
                    sImgPath = uriImg.toString();
                    arrData4Metadata[6] = String.valueOf(sImgPath);
                    arrData4Metadata[7] = String.valueOf(sketerangan);

                    StringBuilder sbFileName = new StringBuilder();
                    sbFileName.append("matadaun_ecv_" + sleaftype + ".csv");
                    sNameFile = sbFileName.toString();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    ehHelper.writeToCSV2(secv, sleaftype, sspad, snitro, dLat, dLon, sketerangan, sNameFile, "MATADAUN/ECV/CSV/", getBaseContext());
                    erasedTempFile();
                    setAllWidgetDisabled(true);

                    btnMetadata.setText("show metadata");
                    btnMetadata.setVisibility(View.VISIBLE);
                    btnMetadata.setEnabled(true);
                }
            }
        });
    }



    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            tvLat.setText(String.valueOf(mLastLocation.getLatitude()));
            tvLong.setText(String.valueOf(mLastLocation.getLongitude()));
            dLat = mLastLocation.getLatitude();
            dLon = mLastLocation.getLongitude();
        } else {
            dLat = 0.0;
            dLon = 0.0;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_temp_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent iSeeData = new Intent(getBaseContext(), MiniCalcActivity.class);
            startActivity(iSeeData);
        }

        return super.onOptionsItemSelected(item);
    }
}
