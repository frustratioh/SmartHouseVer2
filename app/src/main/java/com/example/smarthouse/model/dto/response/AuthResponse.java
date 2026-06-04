package com.example.smarthouse.model.dto.response;

public class AuthResponse {
    private Long userId;
    private String username;
    private String email;

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}