package com.example.registrationpage;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherUpdateService extends Service {
    private static final String API_KEY = "2396d86adbb79407619b6032e5aa6978";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fetchWeatherData();
        return START_NOT_STICKY;
    }
    private void fetchWeatherData() {
        String city = "Kazan";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        WeatherData weatherData = parseWeatherData(response);
                        weatherData.save(this);
                        WeatherWidgetProvider.updateAppWidget(this, AppWidgetManager.getInstance(this), 0);

                    } catch (JSONException e) {
                        Toast.makeText(this, "ошибка", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "ошибка", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    private WeatherData parseWeatherData(JSONObject response) throws JSONException {
        JSONObject main = response.getJSONObject("main");
        double temperature = main.getDouble("temp");
        double feelsLike = main.getDouble("feels_like");
        String location = response.getString("name");

        JSONArray weatherArray = response.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        String iconCode = weatherObject.getString("icon");

        return new WeatherData(String.valueOf(temperature), String.valueOf(feelsLike), location, iconCode);
    }
}