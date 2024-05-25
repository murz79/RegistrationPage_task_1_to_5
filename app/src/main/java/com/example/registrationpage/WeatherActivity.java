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
            String city = cityEditText.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherData(city);
            } else {
                Toast.makeText(WeatherActivity.this, "Введите город", Toast.LENGTH_SHORT).show();
            }
        });

        getLocationButton.setOnClickListener(v -> getLocationAndGetWeather());

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void fetchWeatherData(String city) {
        weatherProgressBar.setVisibility(View.VISIBLE);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" +
                city + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                this::onWeatherDataReceived,
                error -> {
                    weatherProgressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherActivity", "Ошибка: " + error.getMessage(), error);
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void onWeatherDataReceived(JSONObject response) {
        try {
            WeatherData weatherData = WeatherWidgetProvider.parseWeatherData(response);
            weatherData.save(this);
            updateWeatherUI(weatherData);
        } catch (JSONException e) {
            Log.e("WeatherActivity", "Ошибка: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка при парсинге", Toast.LENGTH_SHORT).show();
        } finally {
            weatherProgressBar.setVisibility(View.GONE);
        }
    }

    private void updateWeatherUI(WeatherData weatherData) {
        String weatherInfo = "TEMPERATURE: " + weatherData.getTemperature() + "°C\n"
                + "FEELS LIKE: " + weatherData.getFeelsLike() + "°C\n"
                + "HUMIDITY: " + weatherData.getHumidity() + "%\n"
                + "WIND: " + weatherData.getWindSpeed() + " м/с\n"
                + weatherData.getDescription();

        weatherTextView.setText(weatherInfo);

        String iconUrl = "https://openweathermap.org/img/wn/" + weatherData.getIcon() + "@2x.png";
        Glide.with(this)
                .load(iconUrl)
                .into(weatherIconImageView);
    }

    private void getLocationAndGetWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        fetchWeatherData(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Не удалось получить местоположение", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Ошибка при получении местоположения", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherActivity", "Ошибка: " + e.getMessage(), e);
                });
    }

    private void fetchWeatherData(double latitude, double longitude) {
        weatherProgressBar.setVisibility(View.VISIBLE);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude +
                "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                this::onWeatherDataReceived,
                error -> {
                    weatherProgressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Ошибка получения данных", Toast.LENGTH_SHORT).show();
                    Log.e("WeatherActivity", "Ошибка: " + error.getMessage(), error);
                });
        requestQueue.add(jsonObjectRequest);
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