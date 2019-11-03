package com.example.sriram.weathernow;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class AQIActivity extends AppCompatActivity {
    EditText ET;
    TextView aqi_val, desc, level;
    LinearLayout linearLayout;
    final String key = "6e10c13b2478b2cb534823ec037b0ede6ac80714";
    String link = "https://api.waqi.info/search/?token=%s&keyword=%s";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Air Quality Index");
        setContentView(R.layout.activity_aqi);
        ET = findViewById(R.id.aqi_ET);
        aqi_val = findViewById(R.id.aqiTV);
        desc = findViewById(R.id.aqiDesc);
        level = findViewById(R.id.aqiLevel);
        linearLayout = findViewById(R.id.aqiLinear);
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        ET.setText(city);
    }

    public void getAQI(View view) {
        String final_city = ET.getText().toString();
        String final_link = String.format(link,key,final_city);
        AqiAsyncTask task = new AqiAsyncTask();
        task.execute(final_link);
    }

    protected class AqiAsyncTask extends AsyncTask<String,Void, String[]>{

        @Override
        protected String[] doInBackground(String... strings) {
            String result[]=new String[100];
            try {
                URL url = new URL(strings[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(),"UTF-8"));
                String Jbase = br.readLine();
                JSONObject base = new JSONObject(Jbase);
                JSONArray data = base.getJSONArray("data");
                if(data.isNull(0)){
                    result[0] = "";
                }
                else{
                    result[0] = data.getJSONObject(0).getString("aqi");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if(strings[0].equals("")){
                AlertDialog alertDialog = new AlertDialog.Builder(AQIActivity.this).create();
                alertDialog.setTitle(getString(R.string.en_location_err_title));
                alertDialog.setMessage(getString(R.string.en_aqi_err));
                alertDialog.show();
            }
            else {
                aqi_val.setText(strings[0]);
                if(Integer.parseInt(strings[0])<=50){
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.green));
                    level.setText("Good");
                    desc.setText(getString(R.string.good));
                }
                else if(Integer.parseInt(strings[0])>50 && Integer.parseInt(strings[0])<=100){
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
                    level.setText("Moderate");
                    desc.setText(getString(R.string.moderate));
                }
                else if(Integer.parseInt(strings[0])>100 && Integer.parseInt(strings[0])<=150){
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.orange));
                    level.setText("Unhealthy for sensitive groups");
                    desc.setText(getString(R.string.sensitive));
                }
                else if(Integer.parseInt(strings[0])>150 && Integer.parseInt(strings[0])<=200){
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.mildered));
                    level.setText("Unhealthy");
                    desc.setText(getString(R.string.unhealthy));
                }
                else if(Integer.parseInt(strings[0])>200 && Integer.parseInt(strings[0])<=300){
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.violet));
                    level.setText("Very Unhealthy");
                    desc.setText(getString(R.string.veryunhealthy));
                }
                else if(Integer.parseInt(strings[0])>300){
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.maroon));
                    level.setText("Hazardous");
                    desc.setText(getString(R.string.hazardous));
                }
                else{
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    level.setText("No data");
                    desc.setText("Oops! No data received from station");
                }
            }
        }
    }
}
