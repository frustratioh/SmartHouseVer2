package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
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

public class DeviceHoodActivity extends AppCompatActivity {
    private SeekBar seekPower, seekNoise, seekTimer;
    private ImageView ivPower, ivLight;
    private RadioGroup rgMode;
    private boolean isOn = true, isLightOn = true;
    private Long deviceId;
    private boolean isDirty = false;
    private int currentPower = 70;
    private int currentNoise = 45;
    private int currentTimer = 60;
    private String currentMode = "обычный";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_hood);

        deviceId = getIntent().getLongExtra("deviceId", -1);
        if (deviceId == -1) {
            Toast.makeText(this, "Ошибка: ID устройства не передан", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        seekPower = findViewById(R.id.seekPower);
        seekNoise = findViewById(R.id.seekNoise);
        seekTimer = findViewById(R.id.seekTimer);
        ivPower = findViewById(R.id.ivPower);
        ivLight = findViewById(R.id.ivLight);
        rgMode = findViewById(R.id.rgMode);

        seekPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentPower = progress;
                    isDirty = true;
                    updatePowerDisplay();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekNoise.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentNoise = progress;
                    isDirty = true;
                    updateNoiseDisplay();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekTimer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTimer = progress;
                    isDirty = true;
                    updateTimerDisplay();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ivPower.setOnClickListener(v -> {
            isOn = !isOn;
            ivPower.setImageResource(isOn ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
            isDirty = true;
        });

        ivLight.setOnClickListener(v -> {
            isLightOn = !isLightOn;
            ivLight.setImageResource(isLightOn ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
            isDirty = true;
        });

        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbNormal) {
                currentMode = "обычный";
            } else if (checkedId == R.id.rbTurbo) {
                currentMode = "турбо";
            } else if (checkedId == R.id.rbNight) {
                currentMode = "ночной";
            }
            isDirty = true;
        });

        loadSettings();
    }

    private void updatePowerDisplay() {
        TextView tvPowerValue = findViewById(R.id.tvPowerValue);
        if (tvPowerValue != null) {
            tvPowerValue.setText(currentPower + "%");
        }
    }

    private void updateNoiseDisplay() {
        TextView tvNoiseValue = findViewById(R.id.tvNoiseValue);
        if (tvNoiseValue != null) {
            tvNoiseValue.setText(currentNoise + " дБ");
        }
    }

    private void updateTimerDisplay() {
        TextView tvTimerValue = findViewById(R.id.tvTimerValue);
        if (tvTimerValue != null) {
            tvTimerValue.setText(currentTimer + " сек");
        }
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
                                    case "power":
                                        try {
                                            currentPower = Integer.parseInt(setting.getValue());
                                            seekPower.setProgress(currentPower);
                                            updatePowerDisplay();
                                        } catch (NumberFormatException e) {}
                                        break;
                                    case "noise":
                                        try {
                                            currentNoise = Integer.parseInt(setting.getValue());
                                            seekNoise.setProgress(currentNoise);
                                            updateNoiseDisplay();
                                        } catch (NumberFormatException e) {}
                                        break;
                                    case "timer":
                                        try {
                                            currentTimer = Integer.parseInt(setting.getValue());
                                            seekTimer.setProgress(currentTimer);
                                            updateTimerDisplay();
                                        } catch (NumberFormatException e) {}
                                        break;
                                    case "light":
                                        isLightOn = "on".equals(setting.getValue());
                                        ivLight.setImageResource(isLightOn ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
                                        break;
                                    case "mode":
                                        currentMode = setting.getValue().toLowerCase();
                                        if (currentMode.equals("обычный")) {
                                            rgMode.check(R.id.rbNormal);
                                        } else if (currentMode.equals("охрана") || currentMode.equals("турбо")) {
                                            rgMode.check(R.id.rbTurbo);
                                        } else if (currentMode.equals("ночной")) {
                                            rgMode.check(R.id.rbNight);
                                        }
                                        break;
                                }
                            }
                        } else {
                            Toast.makeText(DeviceHoodActivity.this, "Ошибка загрузки настроек", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DeviceSettingResponse>> call, Throwable t) {
                        Toast.makeText(DeviceHoodActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveSettings() {
        if (!isDirty) {
            finish();
            return;
        }

        java.util.List<com.example.smarthouse.model.dto.request.DeviceSettingRequest> settings = new java.util.ArrayList<>();
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "status", isOn ? "on" : "off"));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "power", String.valueOf(currentPower)));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "noise", String.valueOf(currentNoise)));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "timer", String.valueOf(currentTimer)));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "light", isLightOn ? "on" : "off"));
        settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(deviceId, "mode", currentMode));

        RetrofitClient.getInstance().upsertDeviceSettings(settings)
                .enqueue(new Callback<List<DeviceSettingResponse>>() {
                    @Override
                    public void onResponse(Call<List<DeviceSettingResponse>> call, Response<List<DeviceSettingResponse>> response) {
                        if (response.isSuccessful()) {
                            isDirty = false;
                            Toast.makeText(DeviceHoodActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = "Ошибка сохранения";
                            try { if (response.errorBody() != null) error = response.errorBody().string(); } catch (Exception e) {}
                            android.util.Log.e("DeviceHood", "Save failed: " + error);
                            Toast.makeText(DeviceHoodActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                    @Override
                    public void onFailure(Call<List<DeviceSettingResponse>> call, Throwable t) {
                        android.util.Log.e("DeviceHood", "Save network failure", t);
                        Toast.makeText(DeviceHoodActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
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