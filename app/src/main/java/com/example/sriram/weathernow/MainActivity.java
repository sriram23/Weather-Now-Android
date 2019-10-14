package com.example.sriram.weathernow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView flag_country,WeatherImgView;
    TextView CityTV, Temperature_mainTV,WeatherDescTV,Temp_minTV,Temp_maxTV,WindSpeedTV;
    LocationManager locationManager;
    final String KEY = "7727eb7a7ad3adf1d307938860eca01b";
    String latlon_link;
    String Lat;
    String Lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Temperature_mainTV = findViewById(R.id.TempMain);
        CityTV = findViewById(R.id.City);
        WeatherDescTV = findViewById(R.id.WeatherDesc);
        Temp_minTV = findViewById(R.id.temp_min);
        Temp_maxTV = findViewById(R.id.temp_max);
        WindSpeedTV = findViewById(R.id.WindSpeed);
        WeatherImgView = findViewById(R.id.WeatherImg);

        latlon_link = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&apikey=%s";
        Lat = "11.1045297";
        Lon = "76.9378387";
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        String FinalLink = String.format(latlon_link,Lat,Lon,KEY);
        flag_country = findViewById(R.id.country_flag);
        String flag_link = "https://www.countryflags.io/%s/shiny/64.png";
        CountryAsyncTask task = new CountryAsyncTask();
        task.execute(flag_link);
        WeatherAsyncTask task1 = new WeatherAsyncTask();
        task1.execute(FinalLink);
    }
    public void onLocationChanged(Location location) {
        String Lat = String.valueOf(location.getLatitude());
        String Lng = String.valueOf(location.getLongitude());
        String FinalLatLon_Link = String.format(latlon_link,Lat,Lng,KEY);
        Toast.makeText(this,"Lat:"+Lat+" Lng:"+Lng,Toast.LENGTH_LONG );
    }
    protected class CountryAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... link) {

            Bitmap mIcon_val = null;
            try {
                URL imgURL = new URL(String.format(link[0], "IN"));
                mIcon_val = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());
//                Thread.sleep(5000);
//
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mIcon_val;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            flag_country.setImageBitmap(bitmap);
        }
    }

    protected class WeatherAsyncTask extends AsyncTask<String, Void, String[]> {
        String result[] = new String[100];
        @Override
        protected String[] doInBackground(String... voids) {
            try {
                URL weatherURL = new URL(voids[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(weatherURL.openConnection().getInputStream(),"UTF-8"));
                String JSONBase =  br.readLine();
                JSONObject base = new JSONObject(JSONBase);
                result[0] = base.getString("name");
                result[1] = String.valueOf(base.getJSONObject("main").getDouble("temp"));
                result[2] = base.getJSONArray("weather").getJSONObject(0).getString("main");
                result[3] = base.getJSONArray("weather").getJSONObject(0).getString("description");
                result[4] = String.valueOf(base.getJSONObject("main").getDouble("temp_min"));
                result[5] = String.valueOf(base.getJSONObject("main").getDouble("temp_max"));
                result[6] = base.getJSONObject("wind").getString("speed");
                result[7] = base.getJSONObject("wind").getString("deg");
                result[8] = String.valueOf(getClimate(result[2]));

//                String CityName = base.getString("name");
//                String Temperature = String.valueOf(base.getJSONObject("main").getDouble("temp"));
//                Thread.sleep(5000);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, aVoid[0]+" "+aVoid[1], Toast.LENGTH_SHORT).show();
            Temperature_mainTV.setText(aVoid[1]+"°C");
            GradientDrawable tempCircle = (GradientDrawable) Temperature_mainTV.getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Temperature_mainTV.setBackground(getResources().getDrawable(R.drawable.temp_circle));
//                int color = getTempColor(aVoid[1]);
//                Toast.makeText(MainActivity.this,color,Toast.LENGTH_LONG);
//                tempCircle.setColor(color);
            }
            CityTV.setText(aVoid[0]);
            WeatherDescTV.setText("Weather: "+aVoid[3]);
            Temp_minTV.setText(aVoid[4]+"°C");
            Temp_maxTV.setText(aVoid[5]+"°C");
            WindSpeedTV.setText(aVoid[6]+" m/s");
            WeatherImgView.setImageResource(Integer.parseInt(aVoid[8]));
//            roateImage(WeatherImgView,Integer.parseInt(aVoid[7]));
        }

        int getTempColor(String magnitude){
            int magnitudeColorResourceId=0;
            int magnitudeFloor = Integer.parseInt(magnitude);
            if(magnitudeFloor<27)
                magnitudeColorResourceId = R.color.colorCool;
            if(magnitudeFloor>=27 && magnitudeFloor<30)
                magnitudeColorResourceId = R.color.colorMild;
            if(magnitudeFloor>=30 && magnitudeFloor<33)
                magnitudeColorResourceId = R.color.colorWarm;
            if(magnitudeFloor>=33 && magnitudeFloor<35)
                magnitudeColorResourceId = R.color.colorHot;
            if(magnitudeFloor>=35)
                magnitudeColorResourceId = R.color.ColorVHot;
            return ContextCompat.getColor(MainActivity.this, magnitudeColorResourceId);
        }

        int getClimate(String w){
            int ResId = 0;
            if(w.equals("Haze"))
                ResId = R.drawable.ic_cloudy_day_3;
            if(w.equals("Clouds"))
                ResId = R.drawable.ic_cloudy;
            if(w.equals("Clear"))
                ResId = R.drawable.ic_sunny;
            if(w.equals("Rain"))
                ResId = R.drawable.ic_rainy_6;
            if(w.equals("Thunderstorm"))
                ResId = R.drawable.ic_thunder;
            if(w.equals("Smoke"))
                ResId = R.drawable.ic_snowy_6;
            if(w.equals("Drizzle"))
                ResId = R.drawable.ic_rainy_4;
            if(w.equals("Mist"))
                ResId = R.drawable.ic_snowy_4;
            return ResId;
        }

//        private void roateImage(ImageView imageView,int deg) {
//            Matrix matrix = new Matrix();
//            imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
//            matrix.postRotate((float) deg, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
//            imageView.setImageMatrix(matrix);
//        }
    }
}
