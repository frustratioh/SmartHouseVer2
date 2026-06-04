package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class UpdatePinRequest {
    @SerializedName("pin_code")
    private String pinCode;

    public UpdatePinRequest(String pinCode) {
        this.pinCode = pinCode;
    }
}
