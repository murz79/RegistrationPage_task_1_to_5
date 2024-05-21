package com.example.registrationpage;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeatherData {
    private static final String PREFS_NAME = "weather_data";
    private final String temperature;
    private final String feelsLike;
    private final String location;
    private final String icon;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    String lastUpdated;

    public WeatherData(String temperature, String feelsLike, String location, String icon) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.location = location;
        this.icon = icon;
        this.lastUpdated = formatter.format(calendar.getTime());
    }

    public String getTemperature() {
        return temperature;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public String getLocation() {
        return location;
    }

    public String getIcon() {
        return icon;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void save(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("temperature", temperature);
        editor.putString("feelsLike", feelsLike);
        editor.putString("location", location);
        editor.putString("icon", icon);
        editor.putString("lastUpdated", lastUpdated);
        editor.apply();
    }
}