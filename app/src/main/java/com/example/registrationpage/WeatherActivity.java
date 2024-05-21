package com.example.registrationpage;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String API_KEY = "2396d86adbb79407619b6032e5aa6978";
    private EditText cityEditText;
    private TextView weatherTextView;
    private ImageView weatherIconImageView;
    private RequestQueue requestQueue;
    private FusedLocationProviderClient fusedLocationClient;
    private ProgressBar weatherProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, WeatherUpdateService.class));
        setContentView(R.layout.activity_weather);

        cityEditText = findViewById(R.id.cityEditText);
        Button getWeatherButton = findViewById(R.id.getWeatherButton);
        weatherTextView = findViewById(R.id.weatherTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        Button getLocationButton = findViewById(R.id.getLocationButton);
        requestQueue = Volley.newRequestQueue(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weatherProgressBar = findViewById(R.id.weather_progress_bar);

        getWeatherButton.setOnClickListener(view -> {
            String city = cityEditText.getText().toString();
            if (!city.isEmpty()) {
                getWeatherByCity(city);
            }
        });
        getLocationButton.setOnClickListener(v -> getLocationAndGetWeather());

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void getWeatherByCity(String city) {
        weatherProgressBar.setVisibility(View.VISIBLE);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        double feelsLike = main.getDouble("feels_like");
                        int humidity = main.getInt("humidity");
                        JSONObject wind = response.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");
                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String description = weatherObject.getString("description");
                        String iconCode = weatherObject.getString("icon");
                        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + ".png";

                        String weatherInfo = "TEMPERATURE: " + temperature + "°C\n"
                                + "FEELS LIKE:: " + feelsLike + "°C\n"
                                + "HUMIDITY: " + humidity + "%\n"
                                + "WIND: " + windSpeed + " м/с\n"
                                + description;
                        weatherTextView.setText(weatherInfo);

                        Glide.with(this)
                                .load(iconUrl)
                                .override(500, 500)
                                .into(weatherIconImageView);
                    } catch (JSONException e) {
                        Log.e("0", "Ошибка при парсинге");
                        Toast.makeText(this, "Ошибка при парсинге", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    weatherProgressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(jsonObjectRequest);
        weatherProgressBar.setVisibility(View.GONE);
    }
    private void getLocationAndGetWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            getWeatherByCoordinates(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(this, "Не удалось получить местоположение", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(this, "Ошибка при получении местоположения", Toast.LENGTH_SHORT).show());
        }
    }

    private void getWeatherByCoordinates(double latitude, double longitude) {
        weatherProgressBar.setVisibility(View.VISIBLE);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        double feelsLike = main.getDouble("feels_like");
                        int humidity = main.getInt("humidity");
                        JSONObject wind = response.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");
                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String description = weatherObject.getString("description");
                        String iconCode = weatherObject.getString("icon");
                        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + ".png";

                        String weatherInfo = "TEMPERATURE: " + temperature + "°C\n"
                                + "FEELS LIKE:: " + feelsLike + "°C\n"
                                + "HUMIDITY: " + humidity + "%\n"
                                + "WIND: " + windSpeed + " м/с\n"
                                + description;
                        weatherTextView.setText(weatherInfo);
                        Glide.with(WeatherActivity.this)
                                .load(iconUrl)
                                .override(500, 500)
                                .into(weatherIconImageView);
                    } catch (JSONException e) {
                        Log.e("1", "Ошибка при парсинге");
                        Toast.makeText(this, "Ошибка при парсинге", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    weatherProgressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(jsonObjectRequest);
        weatherProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndGetWeather();
            } else {
                Toast.makeText(this, "Требуется разрешение на доступ к местоположению", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
