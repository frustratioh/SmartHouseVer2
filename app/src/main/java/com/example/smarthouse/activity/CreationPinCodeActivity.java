package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.util.SharedPrefsManager;

public class CreationPinCodeActivity extends AppCompatActivity {
    private String enteredPin = "";
    private ImageView[] dots = new ImageView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_pin_code);
        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);
    }

    public void onNumberClick(View view) {
        String number = ((com.google.android.material.button.MaterialButton) view).getText().toString();
        if (enteredPin.length() < 4) {
            enteredPin += number;
            updateDots();
            if (enteredPin.length() == 4) {
                savePinToServer(enteredPin);
            }
        }
    }

    private void savePinToServer(String pin) {
        long userId = SharedPrefsManager.getUserId(this);
        com.example.smarthouse.model.dto.request.UpdatePinRequest request = 
                new com.example.smarthouse.model.dto.request.UpdatePinRequest(pin);

        com.example.smarthouse.api.RetrofitClient.getInstance().updatePinCode("eq." + userId, request)
                .enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                        SharedPrefsManager.savePinCode(CreationPinCodeActivity.this, pin);
                        Toast.makeText(CreationPinCodeActivity.this, "Пин-код сохранен", Toast.LENGTH_SHORT).show();
                        
                        String address = SharedPrefsManager.getUserAddress(CreationPinCodeActivity.this);
                        if (address == null || address.isEmpty()) {
                            startActivity(new Intent(CreationPinCodeActivity.this, EntryAddressActivity.class));
                        } else {
                            startActivity(new Intent(CreationPinCodeActivity.this, MainActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        // Даже если сеть подвела, сохраняем локально, чтобы пустить пользователя дальше
                        SharedPrefsManager.savePinCode(CreationPinCodeActivity.this, pin);
                        Toast.makeText(CreationPinCodeActivity.this, "Сохранено локально", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreationPinCodeActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(i < enteredPin.length() ? R.drawable.ic_dot_active : R.drawable.ic_dot_non_active);
        }
    }
}