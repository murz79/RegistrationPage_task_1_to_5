package com.example.registrationpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    TextView welcomeTextView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        String login = getIntent().getStringExtra("login");
        welcomeTextView.setText("Welcome" + ", " + login + "!");

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, WeatherActivity.class));
            finish();
        }, 2000);
    }
}