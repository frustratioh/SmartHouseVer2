package com.example.smarthouse.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthouse.R;
import com.example.smarthouse.adapter.RoomAddingAdapter;
import com.example.smarthouse.model.dto.request.CreateRoomRequest;
import com.example.smarthouse.model.dto.request.CustomRoomTypeRequest;
import com.example.smarthouse.model.dto.response.CustomRoomTypeResponse;
import com.example.smarthouse.model.dto.response.RoomTypeResponse;
import com.example.smarthouse.api.RetrofitClient;
import com.example.smarthouse.util.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddingRoomActivity extends AppCompatActivity {
    private EditText etRoomName;
    private RecyclerView recyclerView;
    private RoomAddingAdapter adapter;
    private List<Object> allRoomTypes = new ArrayList<>();
    private Long selectedRoomTypeId = 1L; // Прихожая по умолчанию
    private boolean isCustomTypeSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_room);
        etRoomName = findViewById(R.id.etRoomName);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        initLocalRoomTypes();
        setupAdapter();
        loadAllRoomTypesFromServer();
    }

    private void initLocalRoomTypes() {
        allRoomTypes.clear();
        allRoomTypes.add(new RoomTypeResponse(1L, "Прихожая", ""));
        allRoomTypes.add(new RoomTypeResponse(2L, "Кухня", ""));
        allRoomTypes.add(new RoomTypeResponse(3L, "Гостиная", ""));
        allRoomTypes.add(new RoomTypeResponse(4L, "Ванная", ""));
        allRoomTypes.add(new RoomTypeResponse(5L, "Спальня", ""));
        allRoomTypes.add(new RoomTypeResponse(6L, "Кабинет", ""));
    }

    private void setupAdapter() {
        adapter = new RoomAddingAdapter(allRoomTypes,
                item -> {
                    if (item instanceof RoomTypeResponse) {
                        selectedRoomTypeId = ((RoomTypeResponse) item).getRoomTypeId();
                        isCustomTypeSelected = false;
                    } else if (item instanceof CustomRoomTypeResponse) {
                        selectedRoomTypeId = ((CustomRoomTypeResponse) item).getCustomRoomTypeId();
                        isCustomTypeSelected = true;
                    }
                },
                item -> {
                    if (item instanceof CustomRoomTypeResponse) {
                        CustomRoomTypeResponse type = (CustomRoomTypeResponse) item;
                        new AlertDialog.Builder(this)
                                .setTitle("Удалить тип")
                                .setMessage("Удалить тип \"" + type.getName() + "\"?")
                                .setPositiveButton("Да", (dialog, which) -> deleteCustomRoomType(type.getCustomRoomTypeId()))
                                .setNegativeButton("Нет", null)
                                .show();
                    }
                });
        recyclerView.setAdapter(adapter);
        adapter.setSelectedPosition(0);
    }

    private void loadAllRoomTypesFromServer() {
        long userId = SharedPrefsManager.getUserId(this);

        RetrofitClient.getInstance().getRoomTypes().enqueue(new Callback<List<RoomTypeResponse>>() {
            @Override
            public void onResponse(Call<List<RoomTypeResponse>> call, Response<List<RoomTypeResponse>> response) {
                List<Object> serverTypes = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    serverTypes.addAll(response.body());
                } else {
                    serverTypes.addAll(allRoomTypes); // Оставляем локальные, если на сервере пусто
                }

                RetrofitClient.getInstance().getCustomRoomTypes("eq." + userId).enqueue(new Callback<List<CustomRoomTypeResponse>>() {
                    @Override
                    public void onResponse(Call<List<CustomRoomTypeResponse>> call, Response<List<CustomRoomTypeResponse>> responseCustom) {
                        if (responseCustom.isSuccessful() && responseCustom.body() != null) {
                            serverTypes.addAll(responseCustom.body());
                        }
                        allRoomTypes.clear();
                        allRoomTypes.addAll(serverTypes);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(Call<List<CustomRoomTypeResponse>> call, Throwable t) {
                        allRoomTypes.clear();
                        allRoomTypes.addAll(serverTypes);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void onFailure(Call<List<RoomTypeResponse>> call, Throwable t) {
                // В случае ошибки сети локальные данные уже есть в списке
            }
        });
    }

    public void onAddCustomTypeClick(android.view.View view) {
        showCreateCustomTypeDialog();
    }

    private void showCreateCustomTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новый тип комнаты");
        EditText input = new EditText(this);
        input.setHint("Введите название");
        builder.setView(input);
        builder.setPositiveButton("Создать", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) createCustomRoomType(name);
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    public void saveRoom(android.view.View view) {
        String name = etRoomName.getText().toString().trim();
        if (name.isEmpty() || selectedRoomTypeId == null) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        createRoomInternal(SharedPrefsManager.getUserId(this), name, selectedRoomTypeId, isCustomTypeSelected);
    }

    private void createRoomInternal(long userId, String roomName, Long typeId, boolean isCustom) {
        RetrofitClient.getInstance().createRoom(new CreateRoomRequest(roomName, userId, typeId, isCustom))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            finish();
                        } else {
                            String error = "Ошибка создания";
                            try { if (response.errorBody() != null) error = response.errorBody().string(); } catch (Exception e) {}
                            Toast.makeText(AddingRoomActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AddingRoomActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void deleteCustomRoomType(Long customRoomTypeId) {
        RetrofitClient.getInstance().deleteCustomRoomType("eq." + customRoomTypeId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) loadAllRoomTypesFromServer();
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });
    }

    private void createCustomRoomType(String name) {
        long userId = SharedPrefsManager.getUserId(this);
        RetrofitClient.getInstance().createCustomRoomType(new CustomRoomTypeRequest(name, userId))
                .enqueue(new Callback<List<CustomRoomTypeResponse>>() {
                    @Override
                    public void onResponse(Call<List<CustomRoomTypeResponse>> call, Response<List<CustomRoomTypeResponse>> response) {
                        if (response.isSuccessful()) loadAllRoomTypesFromServer();
                    }
                    @Override
                    public void onFailure(Call<List<CustomRoomTypeResponse>> call, Throwable t) {
                        Toast.makeText(AddingRoomActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void backToMain(android.view.View view) {
        finish();
    }
}
