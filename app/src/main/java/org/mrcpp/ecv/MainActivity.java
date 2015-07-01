package org.mrcpp.ecv;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.mrcpp.other.EHHelper;

import java.io.File;


public class MainActivity extends Activity {

    // EHHElper ivar
    EHHelper ehHelper;

    public MainActivity() {
        ehHelper = new EHHelper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create MATADAUN dir
        File fileECV = new File(Environment.getExternalStorageDirectory(), "MATADAUN/ECV/CSV/");
        if(!fileECV.exists()) {
            fileECV.mkdirs();
        } else {
            Log.i("evan", fileECV.toString());
        }

        LocationManager locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled", Toast.LENGTH_SHORT).show();
        } else {
            ehHelper.showGPSDisabledAlertToUser(MainActivity.this);
        }


        ImageButton btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iTempResult = new Intent(MainActivity.this, TempResultActivity.class);
                startActivity(iTempResult);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
