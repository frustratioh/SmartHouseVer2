package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthouse.R;
import com.example.smarthouse.util.SharedPrefsManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {

            if (SharedPrefsManager.hasPinCode(this)) {

                Intent intent = new Intent(SplashActivity.this, PinCodeActivity.class);
                startActivity(intent);
            } else {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }, 2000);
    }
}