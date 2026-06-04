package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password_hash")
    private String passwordHash;

    public RegisterRequest(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
