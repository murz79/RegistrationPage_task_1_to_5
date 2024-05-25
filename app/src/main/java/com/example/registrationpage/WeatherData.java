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
    private final String humidity;
    private final String windSpeed;
    private final String description;
    private final String lastUpdated;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public WeatherData(String temperature, String feelsLike, String location, String icon, String humidity, String windSpeed, String description) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.location = location;
        this.icon = icon;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.description = description;
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

    public String getHumidity() {
        return humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getDescription() {
        return description;
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
        editor.putString("humidity", humidity);
        editor.putString("windSpeed", windSpeed);
        editor.putString("description", description);
        editor.putString("lastUpdated", lastUpdated);
        editor.apply();
    }
}