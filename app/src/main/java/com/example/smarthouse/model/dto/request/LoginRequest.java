package com.example.smarthouse.model.dto.request;

public class LoginRequest {
    private String email;
    private String hashPassword;

    public LoginRequest(String email, String hashPassword) {
        this.email = email;
        this.hashPassword = hashPassword;
    }
}