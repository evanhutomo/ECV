package org.mrcpp.ecv;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;


public class MetadataActivity extends ActionBarActivity {
    TextView tvECV, tvLat, tvLon, tvGrnPx, tvWhtPx, tvTotPx, tvKet;
    ImageView ivImgData;
    String[] arrData = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metadata);

        tvECV = (TextView) findViewById(R.id.tvResultECV);
        tvLat = (TextView) findViewById(R.id.tvLatResult);
        tvLon = (TextView) findViewById(R.id.tvLongResult);
        tvGrnPx = (TextView) findViewById(R.id.tvPxlGreenResult);
        tvWhtPx = (TextView) findViewById(R.id.tvPxlWhiteResult);
        tvTotPx = (TextView) findViewById(R.id.tvTotalPixelResult);
        tvKet = (TextView) findViewById(R.id.tvKetResult);
        ivImgData = (ImageView) findViewById(R.id.imgLeafTemp);

        if(savedInstanceState == null) {
            Bundle bunExtras = getIntent().getExtras();
            if(bunExtras == null) {
                arrData = null;
            } else {
                arrData = bunExtras.getStringArray("metadata");
            }

        } else {
            arrData = (String[]) savedInstanceState.getSerializable("metadata");
        }

        tvECV.setText(arrData[0]);
        tvLat.setText(arrData[1]);
        tvLon.setText(arrData[2]);
        tvGrnPx.setText(arrData[3]);
        tvWhtPx.setText(arrData[4]);
        tvTotPx.setText(arrData[5]);
        tvKet.setText(arrData[7]);

        Uri uri = Uri.parse(arrData[6]);
        ivImgData.setImageURI(uri);

    }


}
