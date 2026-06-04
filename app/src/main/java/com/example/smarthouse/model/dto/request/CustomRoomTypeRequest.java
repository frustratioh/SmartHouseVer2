package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class CustomRoomTypeRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("user_id")
    private Long userId;

    public CustomRoomTypeRequest(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }
}
