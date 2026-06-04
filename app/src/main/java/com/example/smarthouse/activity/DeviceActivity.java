package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthouse.R;
import com.example.smarthouse.adapter.DeviceMainAdapter;
import com.example.smarthouse.model.dto.request.UpdateDeviceSettingsRequest;
import com.example.smarthouse.model.dto.response.DeviceResponse;
import com.example.smarthouse.model.dto.response.DeviceSettingResponse;
import com.example.smarthouse.model.dto.response.DeviceTypeResponse;
import com.example.smarthouse.api.RetrofitClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceActivity extends AppCompatActivity {
    private static final int REQUEST_DEVICE_SETTINGS = 1;

    private RecyclerView recyclerView;
    private DeviceMainAdapter adapter;
    private List<DeviceResponse> deviceList = new ArrayList<>();
    private Map<Long, String> deviceTypeIcons = new HashMap<>();
    private Long roomId;
    private String roomName;
    private boolean deviceTypesLoaded = false;
    private android.widget.TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        roomId = getIntent().getLongExtra("roomId", -1);
        roomName = getIntent().getStringExtra("roomName");

        tvTitle = findViewById(R.id.tvTitle);
        if (tvTitle != null && roomName != null) {
            tvTitle.setText("Устройства в " + roomName.toLowerCase());
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new DeviceMainAdapter(deviceList,
                device -> openDeviceSettings(device),
                (device, isOn, callback) -> {
                    java.util.List<com.example.smarthouse.model.dto.request.DeviceSettingRequest> settings = new java.util.ArrayList<>();
                    settings.add(new com.example.smarthouse.model.dto.request.DeviceSettingRequest(device.getDeviceId(), "status", isOn ? "on" : "off"));

                    RetrofitClient.getInstance().upsertDeviceSettings(settings)
                            .enqueue(new Callback<List<DeviceSettingResponse>>() {
                                @Override
                                public void onResponse(Call<List<DeviceSettingResponse>> call, Response<List<DeviceSettingResponse>> response) {
                                    if (response.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onError();
                                    }
                                }
                                @Override
                                public void onFailure(Call<List<DeviceSettingResponse>> call, Throwable t) {
                                    callback.onError();
                                }
                            });
                },
                device -> {
                    new AlertDialog.Builder(DeviceActivity.this)
                            .setTitle("Удалить устройство")
                            .setMessage("Вы уверены?")
                            .setPositiveButton("Да", (dialog, which) -> deleteDevice(Long.valueOf(device.getDeviceId())))
                            .setNegativeButton("Нет", null)
                            .show();
                });
        recyclerView.setAdapter(adapter);

        loadDeviceTypes();
    }

    private void openDeviceSettings(DeviceResponse device) {
        Intent intent;
        if (device.getDeviceTypeId() == 5) { // Холодильник
            intent = new Intent(DeviceActivity.this, DeviceWarmFloorActivity.class);
        } else if (device.getDeviceTypeId() == 4) { // Дверь
            intent = new Intent(DeviceActivity.this, DeviceHoodActivity.class);
        } else {
            return;
        }
        intent.putExtra("deviceId", device.getDeviceId());
        startActivityForResult(intent, REQUEST_DEVICE_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DEVICE_SETTINGS) {

            loadDevices();
        }
    }

    private void loadDeviceTypes() {
        RetrofitClient.getInstance().getDeviceTypes().enqueue(new Callback<List<DeviceTypeResponse>>() {
            @Override
            public void onResponse(Call<List<DeviceTypeResponse>> call, Response<List<DeviceTypeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (DeviceTypeResponse type : response.body()) {
                        deviceTypeIcons.put(type.getDeviceTypeId(), type.getImageUrl());
                    }
                    deviceTypesLoaded = true;
                    loadDevices();
                } else {
                    Toast.makeText(DeviceActivity.this, "Ошибка загрузки типов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DeviceTypeResponse>> call, Throwable t) {
                Toast.makeText(DeviceActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDevices() {
        RetrofitClient.getInstance().getRoomDevices("eq." + roomId)
                .enqueue(new Callback<List<DeviceResponse>>() {
                    @Override
                    public void onResponse(Call<List<DeviceResponse>> call, Response<List<DeviceResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            deviceList.clear();
                            deviceList.addAll(response.body());
                            adapter.notifyDataSetChanged(); // Обновляем адаптер, а не создаем новый
                        } else {
                            Toast.makeText(DeviceActivity.this, "Ошибка загрузки устройств", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DeviceResponse>> call, Throwable t) {
                        Toast.makeText(DeviceActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteDevice(Long deviceId) {
        RetrofitClient.getInstance().deleteDevice("eq." + deviceId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadDevices();
                    Toast.makeText(DeviceActivity.this, "Устройство удалено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeviceActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DeviceActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deviceTypesLoaded) {
            loadDevices();
        }
        if (adapter != null) {
            adapter.refreshAllStatuses();
        }
    }

    public void backToMain(android.view.View view) {
        finish();
    }

    public void showAddingDevice(android.view.View view) {
        Intent intent = new Intent(this, AddingDeviceActivity.class);
        intent.putExtra("roomId", roomId);
        startActivity(intent);
    }
}