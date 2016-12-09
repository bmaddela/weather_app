package com.example.bhara.weather.data;

import com.example.bhara.weather.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.apiKey;

/**
 * Created by bhara on 12/5/2016.
 */

public class WeatherHttpClient {

    public String getWeatherData(String place){

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try{
            connection = (HttpURLConnection) (new URL(Utils.BASE_URL + place)).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-api-key","1a2bf9513b76d53d431b489e80b2f8f0");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            //Reading the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }
            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}

