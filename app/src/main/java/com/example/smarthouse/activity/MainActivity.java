package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthouse.R;
import com.example.smarthouse.adapter.RoomMainAdapter;
import com.example.smarthouse.model.dto.response.RoomResponse;
import com.example.smarthouse.api.RetrofitClient;
import com.example.smarthouse.util.SharedPrefsManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomMainAdapter adapter;
    private List<RoomResponse> roomList = new ArrayList<>();
    private TextView tvAddress;

    @Override
    protected void onResume() {
        super.onResume();
        loadAllRooms();
        updateAddress();
    }

    private void updateAddress() {
        String address = SharedPrefsManager.getUserAddress(this);
        if (tvAddress != null) {
            tvAddress.setText(address);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvAddress = findViewById(R.id.tvAddress);
        adapter = new RoomMainAdapter(roomList,
                room -> {
                    Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                    intent.putExtra("roomId", room.getRoomId());
                    intent.putExtra("roomName", room.getName());
                    startActivity(intent);
                },
                roomId -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Удалить комнату")
                            .setMessage("Вы уверены?")
                            .setPositiveButton("Да", (dialog, which) -> deleteRoom(roomId))
                            .setNegativeButton("Нет", null)
                            .show();
                });
        recyclerView.setAdapter(adapter);
        loadAllRooms();
    }

    private void loadAllRooms() {
        long userId = SharedPrefsManager.getUserId(this);

        RetrofitClient.getInstance().getUserRooms("eq." + userId)
                .enqueue(new Callback<List<RoomResponse>>() {
                    @Override
                    public void onResponse(Call<List<RoomResponse>> call, Response<List<RoomResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            roomList.clear();
                            roomList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Ошибка загрузки комнат", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<RoomResponse>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteRoom(Long roomId) {
        RetrofitClient.getInstance().deleteRoom("eq." + roomId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {

                            loadAllRooms();
                            Toast.makeText(MainActivity.this, "Комната удалена", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showAccount(View view) {
        startActivity(new Intent(this, PersonalAccountActivity.class));
    }

    public void showAddingRoom(View view) {
        startActivity(new Intent(this, AddingRoomActivity.class));
    }
}