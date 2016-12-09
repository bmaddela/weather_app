package com.example.bhara.weather.ui.mainactivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bhara.weather.R;
import com.example.bhara.weather.data.CityPreference;
import com.example.bhara.weather.data.JSONWeatherParser;
import com.example.bhara.weather.data.WeatherHttpClient;
import com.example.bhara.weather.model.Weather;
import com.example.bhara.weather.ui.secondactivity.SecondActivity;
import com.example.bhara.weather.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityText);
        temp = (TextView) findViewById(R.id.tempText);
        iconView = (ImageView) findViewById(R.id.thumbnailIcon);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidityText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.riseText);
        sunset = (TextView) findViewById(R.id.setText);
        updated = (TextView) findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);
        renderWeatherData(cityPreference.getCity());

/* PBharoskar code
        TextView updateText = (TextView) findViewById(R.id.updateText);
        updateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });*/

    }

    public void renderWeatherData(String city) {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric"});
    }

    private class DownloadImageAync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String code) {
            final DefaultHttpClient client = new DefaultHttpClient();
            //final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code + ".png");
            String condition = weather.currentCondition.getCondition();
            final HttpGet getRequest;
            switch (condition) {
                case "Rain":
                    getRequest = new HttpGet("http://www.picgifs.com/graphics/r/rain/graphics-rain-301996.gif");
                    break;
                case "Clouds":
                    getRequest = new HttpGet("https://photos.smugmug.com/Bestof/Portfolio/i-dSdKvMD/2/Ti/Partly%20Cloudy%20with%20a%20Chance%20of%20Lenticulars%20-%20Alvord%20Desert,%20Oregon-Ti.jpg");
                    break;
                case "Clear":
                    getRequest = new HttpGet("https://www.tradebit.com/usr/digitalbard/pub/9001/clouds-02-sm.png");
                    break;
                case "Snow":
                    getRequest = new HttpGet("http://www.mikeafford.com/images/weather-graphics/weather-symbols/23-light-snow-shower-day.gif");
                    break;
                case "Smoke":
                    getRequest = new HttpGet("http://cdn2.newsok.biz/cache/sq100-61d3796bf0baf335a4e79789fde3ae3a.jpg");
                    break;
                case "Haze":
                    getRequest = new HttpGet("http://importantrecords.com/sites/default/files/imagecache/recommended_block/imprec/imprec159_skycity_0.jpg");
                    break;
                case "Mist":
                    getRequest = new HttpGet("http://images.8tracks.com/cover/i/000/740/951/themist-5022.jpg?rect=324,0,1296,1296&q=98&fm=jpg&fit=max&w=100&h=100");
                    break;
                default:
                    getRequest = new HttpGet("https://ugotalksalot.files.wordpress.com/2016/06/no-thumb.jpg");
                    break;
            }

            try {
                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.e("Download Image", "Error:" + statusCode);
                    return null;
                }
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));
            weather.iconData = weather.currentCondition.getIcon();
            weather = JSONWeatherParser.getWeather(data);
            Log.v("Data: ", weather.place.getCity());

            new DownloadImageAync().execute(weather.iconData);
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {

            super.onPostExecute(weather);

            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(weather.place.getSunrise());
            String sunsetDate = df.format(weather.place.getSunset());
            String lastUpdateDate = df.format(weather.place.getLastupdate());
            DecimalFormat decimalFormat = new DecimalFormat("#.#");

            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText("" + tempFormat + "°C");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + "hpa");
            wind.setText("Wind: " + weather.wind.getSpeed() + "mph");
            sunrise.setText("Sunrise: " + sunriseDate);
            sunset.setText("Sunset: " + sunsetDate);
            updated.setText("Last Updated: " + lastUpdateDate);
            description.setText("Condition: " + weather.currentCondition.getCondition() + "(" +
                    weather.currentCondition.getDescription() + ")");
        }
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Belmont,US");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());
                String newCity = cityPreference.getCity();
                renderWeatherData(newCity);
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.change_cityId) {
            showInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }

}
