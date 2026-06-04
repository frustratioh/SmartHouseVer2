package com.example.smarthouse.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.model.dto.response.UserResponse;
import com.example.smarthouse.api.RetrofitClient;
import com.example.smarthouse.util.DialogUtil;
import com.example.smarthouse.util.HashUtil;
import com.example.smarthouse.util.SharedPrefsManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private DialogUtil dialogUtil;
    private SharedPreferences loginPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        dialogUtil = new DialogUtil(this);
        loginPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);

        String savedEmail = loginPrefs.getString("email", "");
        String savedPassword = loginPrefs.getString("password", "");
        etEmail.setText(savedEmail);
        etPassword.setText(savedPassword);
    }

    public void login(View view) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
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

        RetrofitClient.getInstance().login("eq." + email, "eq." + hashedPassword)
                .enqueue(new Callback<List<UserResponse>>() {
                    @Override
                    public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            UserResponse user = response.body().get(0);
                            long userId = user.getUserId();
                            SharedPrefsManager.saveUserId(LoginActivity.this, userId);
                            SharedPrefsManager.saveUserData(LoginActivity.this,
                                    user.getUsername(),
                                    user.getEmail(),
                                    user.getAddress());

                            // Синхронизируем ПИН-код из базы в локальное хранилище
                            if (user.getPinCode() != null && !user.getPinCode().isEmpty()) {
                                SharedPrefsManager.savePinCode(LoginActivity.this, user.getPinCode());
                            } else {
                                SharedPrefsManager.clearPinCode(LoginActivity.this);
                            }

                            loginPrefs.edit().putString("email", email).putString("password", password).apply();

                            if (SharedPrefsManager.hasPinCode(LoginActivity.this)) {
                                startActivity(new Intent(LoginActivity.this, PinCodeActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, CreationPinCodeActivity.class));
                            }
                            finish();
                        } else {
                            String errorMsg = "Неверный email или пароль";
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

    public void showRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}