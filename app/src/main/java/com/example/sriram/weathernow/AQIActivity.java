package com.example.sriram.weathernow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AQIActivity extends AppCompatActivity {
    EditText ET;
    final String key = "6e10c13b2478b2cb534823ec037b0ede6ac80714";
    String link = "https://api.waqi.info/search/?token=%s&keyword=%s";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aqi);
        ET = findViewById(R.id.aqi_ET);
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        ET.setText(city);
    }

    public void getAQI(View view) {
        String final_city = ET.getText().toString();
    }
}
