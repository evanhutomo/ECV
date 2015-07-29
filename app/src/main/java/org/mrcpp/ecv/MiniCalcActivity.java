package org.mrcpp.ecv;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class MiniCalcActivity extends ActionBarActivity {
    EditText etV1, etV2, etV3;
    TextView tvAVG;
    String sV1, sV2, sV3;
    Button btnCount;
    String sAVG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_calc);

        etV1 = (EditText) findViewById(R.id.etVal1);
        etV2 = (EditText) findViewById(R.id.etVal2);
        etV3 = (EditText) findViewById(R.id.etVal3);
        tvAVG = (TextView) findViewById(R.id.tvAvgResult);
        btnCount = (Button) findViewById(R.id.btnCount);
        btnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sV1 = String.valueOf(etV1.getText());
                sV2 = String.valueOf(etV2.getText());
                sV3 = String.valueOf(etV3.getText());

                getAvg(sV1, sV2, sV3);
            }
        });
    }


    private void getAvg (String v1, String v2, String v3) {
        double dv1, dv2, dv3, iavg;

        if (!v1.isEmpty() && !v2.isEmpty() && !v3.isEmpty()) {
            dv1 = Double.parseDouble(v1);
            dv2 = Double.parseDouble(v2);
            dv3 = Double.parseDouble(v3);
            iavg = (dv1 + dv2 + dv3) / 3;
            NumberFormat nfAvg = new DecimalFormat("#0.00");

            sAVG = nfAvg.format(iavg);

            tvAVG.setText(String.valueOf(sAVG));
        } else {
            Toast.makeText(this, "please fill the blank field", Toast.LENGTH_SHORT).show();
        }

    }
}
