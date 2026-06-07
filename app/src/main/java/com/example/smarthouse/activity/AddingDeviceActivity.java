package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthouse.R;
import com.example.smarthouse.adapter.DeviceAddingAdapter;
import com.example.smarthouse.model.dto.request.CreateDeviceRequest;
import com.example.smarthouse.model.dto.request.DeviceSettingRequest;
import com.example.smarthouse.model.dto.response.DeviceResponse;
import com.example.smarthouse.model.dto.response.DeviceSettingResponse;
import com.example.smarthouse.model.dto.response.DeviceTypeResponse;
import com.example.smarthouse.api.RetrofitClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddingDeviceActivity extends AppCompatActivity {
    private EditText etDeviceName, etDeviceId;
    private RecyclerView recyclerView;
    private DeviceAddingAdapter adapter;
    private List<DeviceTypeResponse> deviceTypes = new ArrayList<>();
    private Long selectedDeviceTypeId = 4L; // По умолчанию Дверь
    private Long roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_device);
        roomId = getIntent().getLongExtra("roomId", -1);
        etDeviceName = findViewById(R.id.etDeviceName);
        etDeviceId = findViewById(R.id.etDeviceId);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Инициализируем локальными данными сразу, чтобы не было пустого экрана и таймаутов
        initLocalDeviceTypes();
        setupAdapter();

        // Пробуем обновить данные с сервера, если получится
        loadDeviceTypesFromServer();
    }

    private void initLocalDeviceTypes() {
        deviceTypes.clear();
        deviceTypes.add(new DeviceTypeResponse(4L, "Умная дверь"));
        deviceTypes.add(new DeviceTypeResponse(5L, "Холодильник"));
        
        // Заглушки для красоты интерфейса (не кликабельны)
        deviceTypes.add(new DeviceTypeResponse(101L, "Свет"));
        deviceTypes.add(new DeviceTypeResponse(102L, "Кондиционер"));
        deviceTypes.add(new DeviceTypeResponse(103L, "Вытяжка"));
        deviceTypes.add(new DeviceTypeResponse(104L, "Температура"));
        deviceTypes.add(new DeviceTypeResponse(105L, "Вентилятор"));
    }

    private void setupAdapter() {
        List<Long> clickableIds = Arrays.asList(4L, 5L);
        adapter = new DeviceAddingAdapter(deviceTypes,
                deviceTypeId -> selectedDeviceTypeId = deviceTypeId,
                clickableIds);
        recyclerView.setAdapter(adapter);
        
        // Авто-выбор первого (Дверь)
        adapter.setSelectedPosition(0);
        selectedDeviceTypeId = 4L;
    }

    private void loadDeviceTypesFromServer() {
        RetrofitClient.getInstance().getDeviceTypes()
                .enqueue(new Callback<List<DeviceTypeResponse>>() {
                    @Override
                    public void onResponse(Call<List<DeviceTypeResponse>> call, Response<List<DeviceTypeResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<DeviceTypeResponse> serverTypes = new ArrayList<>();
                            for (DeviceTypeResponse type : response.body()) {
                                if (type.getDeviceTypeId() == 4 || type.getDeviceTypeId() == 5) {
                                    serverTypes.add(type);
                                }
                            }
                            if (!serverTypes.isEmpty()) {
                                deviceTypes.clear();
                                deviceTypes.addAll(serverTypes);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DeviceTypeResponse>> call, Throwable t) {
                        // Игнорируем ошибку, так как локальные данные уже есть
                    }
                });
    }

    public void saveDevice(android.view.View view) {
        String name = etDeviceName.getText().toString().trim();
        String uniqueId = etDeviceId.getText().toString().trim();
        if (name.isEmpty() || uniqueId.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDeviceTypeId == null) {
            Toast.makeText(this, "Выберите тип устройства", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roomId == -1) {
            Toast.makeText(this, "Ошибка: комната не определена", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance().createDevice(new CreateDeviceRequest(name, uniqueId, selectedDeviceTypeId, roomId))
                .enqueue(new Callback<List<DeviceResponse>>() {
                    @Override
                    public void onResponse(Call<List<DeviceResponse>> call, Response<List<DeviceResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Long deviceId = response.body().get(0).getDeviceId();
                            initDeviceSettings(deviceId, selectedDeviceTypeId);
                        } else {
                            String error = "Ошибка создания";
                            try {
                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }
                            } catch (Exception e) {}
                            Toast.makeText(AddingDeviceActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DeviceResponse>> call, Throwable t) {
                        Toast.makeText(AddingDeviceActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initDeviceSettings(Long deviceId, Long typeId) {
        List<DeviceSettingRequest> settings = new ArrayList<>();
        if (typeId == 4) { // Дверь
            settings.add(new DeviceSettingRequest(deviceId, "status", "off"));
            settings.add(new DeviceSettingRequest(deviceId, "power", "0"));
            settings.add(new DeviceSettingRequest(deviceId, "noise", "20"));
            settings.add(new DeviceSettingRequest(deviceId, "timer", "30"));
            settings.add(new DeviceSettingRequest(deviceId, "light", "on"));
            settings.add(new DeviceSettingRequest(deviceId, "mode", "обычный"));
        } else if (typeId == 5) { // Холодильник
            settings.add(new DeviceSettingRequest(deviceId, "status", "on"));
            settings.add(new DeviceSettingRequest(deviceId, "temperature", "4"));
            settings.add(new DeviceSettingRequest(deviceId, "mode", "eco"));
            settings.add(new DeviceSettingRequest(deviceId, "motion_sensor", "on"));
            settings.add(new DeviceSettingRequest(deviceId, "timer", "0"));
        }

        RetrofitClient.getInstance().upsertDeviceSettings(settings)
                .enqueue(new Callback<List<DeviceSettingResponse>>() {
                    @Override
                    public void onResponse(Call<List<DeviceSettingResponse>> call, Response<List<DeviceSettingResponse>> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<List<DeviceSettingResponse>> call, Throwable t) {
                        finish(); // Все равно закрываем, устройство уже создано
                    }
                });
    }

    public void backToDevice(android.view.View view) {
        finish();
    }
}
