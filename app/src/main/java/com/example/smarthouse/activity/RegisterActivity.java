package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.model.dto.response.UserResponse;
import com.example.smarthouse.api.RetrofitClient;
import com.example.smarthouse.model.dto.request.RegisterRequest;
import com.example.smarthouse.util.DialogUtil;
import com.example.smarthouse.util.HashUtil;
import com.example.smarthouse.util.SharedPrefsManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword;
    private DialogUtil dialogUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        dialogUtil = new DialogUtil(this);
    }

    public void register(android.view.View view) {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            dialogUtil.showError("Ошибка", "Заполните все поля");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            dialogUtil.showError("Ошибка", "Неверный формат email");
            return;
        }

        String hashedPassword = HashUtil.sha256(password);
        if (hashedPassword == null) {
            dialogUtil.showError("Ошибка", "Ошибка шифрования");
            return;
        }

        RetrofitClient.getInstance().register(new RegisterRequest(email, hashedPassword))
                .enqueue(new Callback<List<UserResponse>>() {
                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            UserResponse user = response.body().get(0);
                            long userId = user.getUserId();
                            SharedPrefsManager.saveUserId(RegisterActivity.this, userId);
                            SharedPrefsManager.saveUserData(RegisterActivity.this, user.getUsername(), email, null);
                            startActivity(new Intent(RegisterActivity.this, CreationPinCodeActivity.class));
                            finish();
                        } else {
                            String errorMsg = "Ошибка регистрации";
                            try {
                                if (response.errorBody() != null) errorMsg = response.errorBody().string();
                            } catch (Exception e) {}
                            dialogUtil.showError("Ошибка", errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                        dialogUtil.showError("Ошибка сети", t.getMessage());
                    }
                });
    }

    public void showLogin(android.view.View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}