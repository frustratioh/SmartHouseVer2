package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class UpdateAccountRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    public UpdateAccountRequest(String email, String address) {
        this.email = email;
        this.address = address;
    }

    public String getEmail() { return email; }
    public String getAddress() { return address; }
}
