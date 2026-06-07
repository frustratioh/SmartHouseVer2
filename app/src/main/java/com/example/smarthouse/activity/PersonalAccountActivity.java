package com.example.smarthouse.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.smarthouse.R;
import com.example.smarthouse.api.RetrofitClient;
import com.example.smarthouse.model.dto.request.UpdateAccountRequest;
import com.example.smarthouse.model.dto.response.UserResponse;
import com.example.smarthouse.util.SharedPrefsManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalAccountActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etUsername, etEmail, etAddress;
    private ImageView ivAvatar;
    private Uri selectedImageUri = null;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        ivAvatar = findViewById(R.id.ivAvatar);

        userId = SharedPrefsManager.getUserId(this);

        loadData();
    }

    private void loadData() {

        etUsername.setText(SharedPrefsManager.getUserName(this));
        etEmail.setText(SharedPrefsManager.getUserEmail(this));
        etAddress.setText(SharedPrefsManager.getUserAddress(this));

        loadAvatar();

        loadFromServer();
    }

    private void loadFromServer() {
        RetrofitClient.getInstance().getUser("eq." + userId)
                .enqueue(new Callback<List<UserResponse>>() {
                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            UserResponse user = response.body().get(0);
                            etUsername.setText(user.getUsername());
                            etEmail.setText(user.getEmail());
                            if (user.getAddress() != null) {
                                etAddress.setText(user.getAddress());
                            }

                            SharedPrefsManager.saveUserData(PersonalAccountActivity.this,
                                    user.getUsername(), user.getEmail(), user.getAddress());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {}
                });
    }

    private void loadAvatar() {
        String avatarBase64 = SharedPrefsManager.getAvatar(this);
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            byte[] avatarBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
            Glide.with(this).load(avatarBytes).circleCrop().into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_avatar);
        }
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(ivAvatar);
        }
    }

    public void saveUserData(View view) {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = getBytes(inputStream);
                String avatarBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                SharedPrefsManager.saveAvatar(this, avatarBase64);
                selectedImageUri = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SharedPrefsManager.saveUserData(this, username, email, address);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Сохранить изменения?")
                .setMessage("Вы уверены, что хотите изменить данные профиля?")
                .setPositiveButton("Да", (dialog, which) -> {
                    UpdateAccountRequest request = new UpdateAccountRequest(email, address);
                    RetrofitClient.getInstance().updateUserAccount("eq." + userId, request)
                            .enqueue(new Callback<List<UserResponse>>() {
                                @Override
                                public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(PersonalAccountActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();

                                        if (response.body() != null && !response.body().isEmpty()) {
                                            UserResponse user = response.body().get(0);
                                            SharedPrefsManager.saveUserData(PersonalAccountActivity.this,
                                                    user.getUsername(), user.getEmail(), user.getAddress());
                                        }
                                    } else {

                                        try {
                                            Toast.makeText(PersonalAccountActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        loadData();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                                    Toast.makeText(PersonalAccountActivity.this, "Сохранено локально", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public void logout(View view) {
        SharedPrefsManager.clearAll(this);
        SharedPrefsManager.saveAvatar(this, "");
        getSharedPreferences("login_prefs", MODE_PRIVATE).edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void backToMain(View view) {
        finish();
    }
}