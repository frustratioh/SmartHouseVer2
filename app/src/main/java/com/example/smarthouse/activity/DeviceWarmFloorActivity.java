package com.example.smarthouse.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.model.dto.request.UpdateDeviceSettingsRequest;
import com.example.smarthouse.api.RetrofitClient;
import com.example.smarthouse.model.dto.response.DeviceSettingResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceWarmFloorActivity extends AppCompatActivity {
    private static final String TAG = "DeviceWarmFloor";
    private TextView tvTemp, tvTimerValue;
    private SeekBar seekTimer;
    private ImageView ivPower, ivMotion;
    private RadioGroup rgMode;
    private Long deviceId;
    private boolean isDirty = false;
    private int currentTemp = 22;
    private int currentTimer = 120;
    private String currentMode = "comfort";
    private boolean isOn = true;
    private boolean isMotionOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_warm_floor);

        deviceId = getIntent().getLongExtra("deviceId", -1);
        Log.d(TAG, "onCreate, deviceId=" + deviceId);

        if (deviceId == -1) {
            Toast.makeText(this, "Ошибка: ID устройства не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTemp = findViewById(R.id.tvTemp);
        tvTimerValue = findViewById(R.id.tvTimerValue);
        seekTimer = findViewById(R.id.seekTimer);
        ivPower = findViewById(R.id.ivPower);
        ivMotion = findViewById(R.id.ivMotion);
        rgMode = findViewById(R.id.rgMode);

        findViewById(R.id.btnMinus).setOnClickListener(v -> changeTemp(-1));
        findViewById(R.id.btnPlus).setOnClickListener(v -> changeTemp(1));

        ivPower.setOnClickListener(v -> {
            isOn = !isOn;
            ivPower.setImageResource(isOn ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
            isDirty = true;
        });

        ivMotion.setOnClickListener(v -> {
            isMotionOn = !isMotionOn;
            ivMotion.setImageResource(isMotionOn ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
            isDirty = true;
        });

        seekTimer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTimer = progress;
                    isDirty = true;
                    if (tvTimerValue != null) {
                        tvTimerValue.setText(progress + " мин");
                    }
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbEco) {
                currentMode = "eco";
            } else if (checkedId == R.id.rbComfort) {
                currentMode = "comfort";
            } else if (checkedId == R.id.rbMax) {
                currentMode = "max";
            }
            isDirty = true;
        });

        loadSettings();
    }

    private void loadSettings() {
        RetrofitClient.getInstance().getDeviceSettings("eq." + deviceId)
                .enqueue(new Callback<List<DeviceSettingResponse>>() {
                    @Override
                    public void onResponse(Call<List<DeviceSettingResponse>> call, Response<List<DeviceSettingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (DeviceSettingResponse setting : response.body()) {
                                switch (setting.getParameter()) {
                                    case "status":
                                        isOn = "on".equals(setting.getValue());
                                        ivPower.setImageResource(isOn ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
                                        break;
                                    case "temperature":
                                        try {
                                            currentTemp = Integer.parseInt(setting.getValue());
                                            tvTemp.setText(currentTemp + "°C");
                                        } catch (NumberFormatException e) {}
                                        break;
                                    case "timer":
                                        try {
                                            currentTimer = Integer.parseInt(setting.getValue());
                                            seekTimer.setProgress(currentTimer);
                                            if (tvTimerValue != null) {
                                                tvTimerValue.setText(currentTimer + " сек");
                                            }
                                        } catch (NumberFormatException e) {}
                                        break;
                                    case "mode":
                                        currentMode = setting.getValue().toLowerCase();
                                        if (currentMode.equals("eco")) {
                                            rgMode.check(R.id.rbEco);
                                        } else if (currentMode.equals("comfort")) {
                                            rgMode.check(R.id.rbComfort);
                                        } else if (currentMode.equals("max")) {
                                            rgMode.check(R.id.rbMax);
                                        }
                                        break;
                                    case "motion_sensor":
                                        isMotionOn = "on".equals(setting.getValue());
                                        ivMotion.setImageResource(isMotionOn ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
                                        break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<List<DeviceSettingResponse>> call, Throwable t) {
                        Toast.makeText(DeviceWarmFloorActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void changeTemp(int delta) {
        currentTemp += delta;
        if (currentTemp < 16) currentTemp = 16;
        if (currentTemp > 30) currentTemp = 30;
        tvTemp.setText(currentTemp + "°C");
        isDirty = true;
    }

    private void saveSettings() {
        if (!isDirty) return;

        java.util.List<com.example.smarthouse.model.dto.request.DeviceSettingRequest> settings = new java.util.ArrayList<>();
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "status", isOn ? "on" : "off"));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "temperature", String.valueOf(currentTemp)));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "timer", String.valueOf(currentTimer)));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "mode", currentMode));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "motion_sensor", isMotionOn ? "on" : "off"));

        RetrofitClient.getInstance().upsertDeviceSettings(settings)
                .enqueue(new Callback<List<DeviceSettingResponse>>() {
                    @Override
                    public void onResponse(Call<List<DeviceSettingResponse>> call, Response<List<DeviceSettingResponse>> response) {
                        if (response.isSuccessful()) {
                            isDirty = false;
                            Toast.makeText(DeviceWarmFloorActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = "Ошибка сохранения";
                            try { if (response.errorBody() != null) error = response.errorBody().string(); } catch (Exception e) {}
                            android.util.Log.e("DeviceWarmFloor", "Save failed: " + error);
                            Toast.makeText(DeviceWarmFloorActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                    @Override
                    public void onFailure(Call<List<DeviceSettingResponse>> call, Throwable t) {
                        android.util.Log.e("DeviceWarmFloor", "Save failure", t);
                        Toast.makeText(DeviceWarmFloorActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    public void backToDevice(View view) {
        if (isDirty) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Сохранить изменения?")
                    .setMessage("Вы внесли изменения. Хотите применить их?")
                    .setPositiveButton("Да", (dialog, which) -> saveSettings())
                    .setNegativeButton("Нет", (dialog, which) -> finish())
                    .setNeutralButton("Отмена", null)
                    .show();
        } else {
            finish();
        }
    }
}