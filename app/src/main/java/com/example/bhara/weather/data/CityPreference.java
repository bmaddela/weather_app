package com.example.bhara.weather.data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by bhara on 12/6/2016.
 */

public class CityPreference {

    SharedPreferences pref;

    public CityPreference(Activity activity){
        pref = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return pref.getString("city", "Belmont,US");
    }

    public void setCity(String city){
        pref.edit().putString("city", city).commit();
    }
}
