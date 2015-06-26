package org.mrcpp.ecv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.mrcpp.constant.EHConstant;
import org.mrcpp.other.EHHelper;


public class TempResultActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    Spinner spnLeafType;
    EditText etSPAD;
    EditText etNitro;
    ImageView imgview;
    TextView tvResultECV;
    EHConstant ehLeafTitle;

    String sleaftype, sspad, snitro, secv, sdate;
    Double CV;

    public TempResultActivity() {
        ehLeafTitle = new EHConstant();
        ehHelper = new EHHelper();
    }

    // EHHElper ivar
    EHHelper ehHelper;
    String sNameFile;

    // Google API ivar
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView tvLat, tvLong;
    Double dLat, dLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_result);

        //google sync lat long
        tvLat = (TextView) findViewById(R.id.tvLatResult);
        tvLong = (TextView) findViewById(R.id.tvLongResult);

        buildGoogleApiClient();
        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
        }

        spnLeafType = (Spinner) findViewById(R.id.spnLeaftype);
        etSPAD = (EditText) findViewById(R.id.etSPAD);
        etNitro = (EditText) findViewById(R.id.etNitro);
        imgview = (ImageView) findViewById(R.id.imgLeafTemp);
        tvResultECV = (TextView) findViewById(R.id.tvResultECV);

        Intent iCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iCamera, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            final Button btnSave = (Button) findViewById(R.id.btnSave);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Bitmap bmp = (Bitmap)data.getExtras().get("data");
            imgview.setImageBitmap(bmp);

            double AvRs=0,AvGs=0,AvBs=0,AvRr=0,AvGr=0,AvBr=0,Er=0,Eg=0,Eb=0,Rfix=0,Gfix=0,Bfix=0;

            int jumlahHijau = 0;
            int jumlahPutih = 0;
            int Rs=0,Gs=0,Bs=0,Rr=0,Gr=0,Br=0;

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
                }
            }
            if(jumlahHijau == 0 || jumlahPutih == 0) {
                AlertDialog.Builder abCSVNull = new AlertDialog.Builder(this);
                abCSVNull.setMessage("Please arrange camera into the leaf object properly");
                abCSVNull.setCancelable(true);
                abCSVNull.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TempResultActivity.this.finish();
                    }
                });

                AlertDialog ad = abCSVNull.create();
                ad.show();
            }

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

            tvResultECV.setText(String.format("%3f", CV));

            onSaveTheData();

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Intent menu = new Intent(TempResultActivity.this, MainActivity.class);
            startActivity(menu);
            TempResultActivity.this.finish();
        }
    }

    public Boolean verifyData() {
        String sErr;

        sleaftype = String.valueOf(spnLeafType.getSelectedItem());
        sspad = String.valueOf(etSPAD.getText());
        snitro = String.valueOf(etNitro.getText());

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

    public void onSaveTheData() {
        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyData()) {
                    Log.i("evanhutomo", "sukses");
                    StringBuilder sbFileName = new StringBuilder();
                    sbFileName.append("matadaun_ecv_" + sleaftype + ".csv");
                    sNameFile = sbFileName.toString();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    ehHelper.writeToCSV2(secv, sleaftype, sspad, snitro, dLat, dLon, sNameFile, "MATADAUN/ECV/", getBaseContext());
                    btnSave.setText("SAVED!");
                    btnSave.setClickable(false);
                    
                }

            }
        });


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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
