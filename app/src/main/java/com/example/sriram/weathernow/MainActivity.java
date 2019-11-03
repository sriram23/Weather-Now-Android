package com.example.sriram.weathernow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView flag_country,WeatherImgView;
    TextView CityTV, Temperature_mainTV,WeatherDescTV,Temp_minTV,Temp_maxTV,WindSpeedTV,Sunrise,Sunset,LastUpdate,WindTV;
    LocationManager locationManager;
    ScrollView scroll;
    ProgressBar pbar;
    final String KEY = "7727eb7a7ad3adf1d307938860eca01b";
    String latlon_link;
    String yandex_trans;
    String lang;
    final String KEY_YANDEX = "trnsl.1.1.20191019T094233Z.0ba61e7230e4586e.2e50d940f6626b2fb2a0c141226169539b91aeef";
    String Lat;
    String Lon;
    String Con;
    String flag_link;
    EditText et;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Con = "IN";
        Temperature_mainTV = findViewById(R.id.TempMain);
        CityTV = findViewById(R.id.City);
        WeatherDescTV = findViewById(R.id.WeatherDesc);
        Temp_minTV = findViewById(R.id.temp_min);
        Temp_maxTV = findViewById(R.id.temp_max);
        WindSpeedTV = findViewById(R.id.WindSpeed);
        WeatherImgView = findViewById(R.id.WeatherImg);
        Sunrise = findViewById(R.id.sunriseTV);
        Sunset = findViewById(R.id.sunsetTV);
        LastUpdate = findViewById(R.id.lastUpdate);
        WindTV = findViewById(R.id.windTextView);
        scroll = findViewById(R.id.scrollView);
        pbar = findViewById(R.id.progress);
        et = findViewById(R.id.CitySearch);
        scroll.setVisibility(View.INVISIBLE);
        btn = findViewById(R.id.SearchBtn);
        latlon_link = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&apikey=%s";
        yandex_trans = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%s&text=%s&lang=%s";
        flag_link = "https://www.countryflags.io/%s/shiny/64.png";
        lang = "en";
//        Lat = "11.1045297";
//        Lon = "76.9378387";
        Lat = "11.0165332";
        Lon = "76.9694461";
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
        CountryAsyncTask task = new CountryAsyncTask();
        task.execute(flag_link,Con);
        WeatherAsyncTask task1 = new WeatherAsyncTask();
        task1.execute(FinalLink);
    }
    public void onLocationChanged(Location location) {
        String Lat = String.valueOf(location.getLatitude());
        String Lng = String.valueOf(location.getLongitude());
        String FinalLatLon_Link = String.format(latlon_link,Lat,Lng,KEY);
    }

    public void getLatLon(View view) {
        try {
//            EditText et = findViewById(R.id.CitySearch);
            String location = et.getText().toString();
            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(location, 1);
            if(list.isEmpty()){
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(getString(R.string.en_location_err_title));
                alertDialog.setMessage(getString(R.string.en_location_err));
                alertDialog.show();
            }
            else {
                Address address = list.get(0);
                String locality = address.getLocality();
                Lat = String.valueOf(address.getLatitude());
                Lon = String.valueOf(address.getLongitude());
                String FinalLink = String.format(latlon_link, Lat, Lon, KEY);
                WeatherAsyncTask task = new WeatherAsyncTask();
                task.execute(FinalLink);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveToAQI(View view) {
        Intent intent = new Intent(MainActivity.this,AQIActivity.class);
        intent.putExtra("city",CityTV.getText().toString());
        startActivity(intent);
    }

//    public void gotoYandex(View view) {
////        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("translate.yandex.com")));
//        WebView webView = findViewById(R.id.webV);
//        webView.loadUrl("translate.yandex.com");
//    }


    protected class CountryAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... link) {

            Bitmap mIcon_val = null;
            try {
                URL imgURL = new URL(String.format(link[0], Con));
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
//                scroll.setVisibility(View.GONE);
//                pbar.setVisibility(View.VISIBLE);
                URL weatherURL = new URL(voids[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(weatherURL.openConnection().getInputStream(),"UTF-8"));
                String JSONBase =  br.readLine();
                JSONObject base = new JSONObject(JSONBase);
                result[0] = base.getString("name");
                result[1] = String.valueOf((int)base.getJSONObject("main").getDouble("temp"));
                result[2] = base.getJSONArray("weather").getJSONObject(0).getString("main");
                result[3] = base.getJSONArray("weather").getJSONObject(0).getString("description");
                result[4] = String.valueOf(base.getJSONObject("main").getDouble("temp_min"));
                result[5] = String.valueOf(base.getJSONObject("main").getDouble("temp_max"));
                result[6] = base.getJSONObject("wind").getString("speed");
                result[7] = base.getJSONObject("wind").getString("deg");
                result[8] = String.valueOf(getClimate(result[2]));
                result[9] = convertTime(base.getJSONObject("sys").getLong("sunrise"));
                result[10] = convertTime(base.getJSONObject("sys").getLong("sunset"));
                if(lang.equals("ta")) {
//                    result[11] = translate_ta(getString(R.string.ta_lastupdate) + convertTime(base.getLong("dt")));
                }
                else{
                    result[11] = getString(R.string.en_lastupdate) + convertTime(base.getLong("dt"));
                }
                result[12] = getString(R.string.en_wind);
                result[13] = base.getJSONObject("sys").getString("country");

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
        protected void onPreExecute() {
            super.onPreExecute();
            scroll.setVisibility(View.GONE);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String[] aVoid) {
            super.onPostExecute(aVoid);
            Temperature_mainTV.setText(aVoid[1]+"°C");
            GradientDrawable tempCircle = (GradientDrawable) Temperature_mainTV.getBackground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Temperature_mainTV.setBackground(getResources().getDrawable(R.drawable.temp_circle));
            }
            CityTV.setText(aVoid[0]);
            WeatherDescTV.setText(aVoid[3]);
            Temp_minTV.setText(aVoid[4]+"°C");
            Temp_maxTV.setText(aVoid[5]+"°C");
            WindSpeedTV.setText(aVoid[6]+" m/s");
            WeatherImgView.setImageResource(Integer.parseInt(aVoid[8]));
            Sunrise.setText(aVoid[9]);
            Sunset.setText(aVoid[10]);
            LastUpdate.setText(aVoid[11]);
            WindTV.setText(aVoid[12]);
            scroll.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.GONE);
            Con = aVoid[13];
            String flag_link = "https://www.countryflags.io/%s/shiny/64.png";
            if(lang.equals("en")){
                et.setHint(getString(R.string.en_hint_text));
                btn.setText(R.string.en_search);
            }
            else{
                et.setHint(getString(R.string.ta_hint_text));
                btn.setText(R.string.ta_search);
            }
            CountryAsyncTask task = new CountryAsyncTask();
            task.execute(flag_link,aVoid[13]);

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

        String convertTime(long epoch){
            Date date = new Date(epoch*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:MM a z");
            String human = sdf.format(date);
            return human;
        }

//        private String translate_ta(String name) {
//            String resp = null;
//            try {
//                URL transURL = new URL(String.format(yandex_trans, KEY_YANDEX, name, lang));
//                BufferedReader br = new BufferedReader(new InputStreamReader(transURL.openConnection().getInputStream(), "UTF-8"));
//                String Jresp = br.readLine();
//                JSONObject base = new JSONObject(Jresp);
//                resp = base.getJSONArray("text").getString(0);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return resp;
//        }
    }

}
