package com.example.smarthouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.api.RetrofitClient;
import com.example.smarthouse.util.DialogUtil;
import com.example.smarthouse.util.SharedPrefsManager;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntryAddressActivity extends AppCompatActivity {
    private MaterialAutoCompleteTextView etAddress;
    private DialogUtil dialogUtil;

    private String[] addressSuggestions = {
            "г. Москва, ул. Тверская, д. ",
            "г. Санкт-Петербург, ул. Невский, д. ",
            "г. Омск, ул. Ленина, д. "
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_address);

        etAddress = findViewById(R.id.etAddress);
        dialogUtil = new DialogUtil(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, addressSuggestions);
        etAddress.setAdapter(adapter);
        etAddress.setThreshold(1);

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();

                if (text.equals("г")) {
                    etAddress.setText("г. ");
                    etAddress.setSelection(etAddress.getText().length());
                } else if (text.endsWith("ул") && !text.contains("ул.")) {
                    String newText = text.substring(0, text.length() - 2) + "ул. ";
                    etAddress.setText(newText);
                    etAddress.setSelection(etAddress.getText().length());
                } else if (text.endsWith("д") && !text.contains("д.") && !text.endsWith("ад")) {
                    etAddress.setText(text + ". ");
                    etAddress.setSelection(etAddress.getText().length());
                } else if (text.endsWith("кв") && !text.contains("кв.")) {
                    etAddress.setText(text + ". ");
                    etAddress.setSelection(etAddress.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void saveAddress(android.view.View view) {
        String address = etAddress.getText().toString().trim();

        if (address.isEmpty()) {
            dialogUtil.showError("Ошибка", "Введите адрес");
            return;
        }

        address = formatAddress(address);
        address = address.replaceAll("\\s+", " ").trim();

        if (address.startsWith("\"") && address.endsWith("\"")) {
            address = address.substring(1, address.length() - 1);
        }

        String missingParts = getMissingAddressParts(address);
        if (!missingParts.isEmpty()) {
            dialogUtil.showError("Неполный адрес", "Пожалуйста, укажите:\n" + missingParts);
            return;
        }

        final String finalAddress = address;
        long userId = SharedPrefsManager.getUserId(this);
        final String currentUsername = SharedPrefsManager.getUserName(this);
        final String currentEmail = SharedPrefsManager.getUserEmail(this);

        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("address", finalAddress);

        RetrofitClient.getInstance().updateAddress("eq." + userId, body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            SharedPrefsManager.saveUserData(EntryAddressActivity.this,
                                    currentUsername, currentEmail, finalAddress);

                            startActivity(new Intent(EntryAddressActivity.this, MainActivity.class));
                            finish();
                        } else {
                            dialogUtil.showError("Ошибка", "Не удалось сохранить адрес");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dialogUtil.showError("Ошибка сети", t.getMessage());
                    }
                });
    }

    private String getMissingAddressParts(String address) {
        StringBuilder missing = new StringBuilder();

        if (!address.matches(".*г\\.\\s*[А-Яа-яA-Za-z-]+.*")) {
            missing.append(" - город\n");
        }

        if (!address.matches(".*ул\\.\\s*[А-Яа-яA-Za-z-]+.*")) {
            missing.append(" - улицу\n");
        }

        if (!address.matches(".*д\\.\\s*\\d+.*")) {
            missing.append(" - номер дома\n");
        }

        // Квартира теперь не обязательна для частных домов
        return missing.toString();
    }

    private String formatAddress(String address) {
        if (address.matches("^г[^.]")) {
            address = address.replaceFirst("^г", "г.");
        }
        if (address.contains("ул") && !address.contains("ул.")) {
            address = address.replace("ул", "ул.");
        }
        if (address.contains("д") && !address.contains("д.") && !address.contains("дом")) {
            address = address.replace("д", "д.");
        }
        if (address.contains("кв") && !address.contains("кв.")) {
            address = address.replace("кв", "кв.");
        }
        return address;
    }

    public void backToMain(android.view.View view) {
        finish();
    }
}