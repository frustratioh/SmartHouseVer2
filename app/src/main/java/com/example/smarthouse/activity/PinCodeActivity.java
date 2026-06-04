package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.util.SharedPrefsManager;

public class PinCodeActivity extends AppCompatActivity {
    private String enteredPin = "";
    private ImageView[] dots = new ImageView[4];
    private String savedPinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);
        savedPinCode = SharedPrefsManager.getPinCode(this);
    }

    public void onNumberClick(View view) {
        String number = ((com.google.android.material.button.MaterialButton) view).getText().toString();
        if (enteredPin.length() < 4) {
            enteredPin += number;
            updateDots();
            if (enteredPin.length() == 4) {
                if (enteredPin.equals(savedPinCode)) {

                    String address = SharedPrefsManager.getUserAddress(this);
                    if (address == null || address.isEmpty()) {

                        startActivity(new Intent(PinCodeActivity.this, EntryAddressActivity.class));
                    } else {
                        Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PinCodeActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Неверный пин-код", Toast.LENGTH_SHORT).show();
                    enteredPin = "";
                    updateDots();
                }
            }
        }
    }

    public void logout(View view) {
        SharedPrefsManager.clearAll(this);
        getSharedPreferences("login_prefs", MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(i < enteredPin.length() ? R.drawable.ic_dot_active : R.drawable.ic_dot_non_active);
        }
    }
}