package com.example.smarthouse.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("id")
    private Long userId;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("password_hash")
    private String passwordHash;

    @SerializedName("pin_code")
    private String pinCode;

    // username column is missing in DB, we'll use email as username for display
    public Long getUserId() { return userId; }
    public String getUsername() { return email != null ? email.split("@")[0] : "User"; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPinCode() { return pinCode; }
}
