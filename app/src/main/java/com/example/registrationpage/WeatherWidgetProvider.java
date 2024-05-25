package com.example.registrationpage;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class WeatherWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_UPDATE_WIDGET = "com.example.registrationpage.ACTION_UPDATE_WIDGET";
    private static final String API_KEY = "2396d86adbb79407619b6032e5aa6978";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, WeatherWidgetProvider.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent updateIntent = new Intent(context, WeatherWidgetProvider.class);
        updateIntent.setAction(ACTION_UPDATE_WIDGET);
        views.setOnClickPendingIntent(R.id.widget_update_button, PendingIntent.getBroadcast(
                context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        fetchWeatherData(context, views, appWidgetManager, appWidgetId);
    }

    private static void fetchWeatherData(Context context, RemoteViews views,
                                         AppWidgetManager appWidgetManager, int appWidgetId) {
        views.setViewVisibility(R.id.widget_progress_bar, View.VISIBLE);
        String city = "Kazan";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        WeatherData weatherData = parseWeatherData(response);
                        weatherData.save(context);
                        updateWidgetUI(context, views, appWidgetManager, appWidgetId, weatherData);
                    } catch (JSONException e) {
                        Toast.makeText(context, "ошибка", Toast.LENGTH_SHORT).show();
                    } finally {
                        views.setViewVisibility(R.id.widget_progress_bar, View.GONE);
                    }
                    },
                error -> {
                    views.setViewVisibility(R.id.widget_progress_bar, View.GONE);
                    Toast.makeText(context, "ошибка", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(jsonObjectRequest);
    }

    public static WeatherData parseWeatherData(JSONObject response) throws JSONException {
        JSONObject main = response.getJSONObject("main");
        double temperature = main.getDouble("temp");
        double feelsLike = main.getDouble("feels_like");
        String location = response.getString("name");
        JSONArray weatherArray = response.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        String iconCode = weatherObject.getString("icon");
        int humidity = main.getInt("humidity");
        double windSpeed = response.getJSONObject("wind").getDouble("speed");
        String description = response.getJSONArray("weather").getJSONObject(0).getString("description");

        return new WeatherData(String.valueOf(temperature),
                String.valueOf(feelsLike),
                location,
                iconCode,
                String.valueOf(humidity),
                String.valueOf(windSpeed),
                description);
    }

    private static void updateWidgetUI(Context context, RemoteViews views,
                                       AppWidgetManager appWidgetManager, int appWidgetId, WeatherData weatherData) {
        views.setTextViewText(R.id.widget_temperature, weatherData.getTemperature() + "°C");
        views.setTextViewText(R.id.widget_feels_like, "Feels like " + weatherData.getFeelsLike() + "°C");
        views.setTextViewText(R.id.widget_location, weatherData.getLocation());
        views.setTextViewText(R.id.widget_last_updated, "Last Updated: " + weatherData.getLastUpdated());

        String iconUrl = "https://openweathermap.org/img/wn/" + weatherData.getIcon() + ".png";

        AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widget_icon, views, appWidgetId);

        Glide.with(context)
                .asBitmap()
                .load(iconUrl)
                .into(appWidgetTarget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}