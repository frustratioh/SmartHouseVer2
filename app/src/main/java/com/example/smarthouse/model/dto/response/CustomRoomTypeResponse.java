package com.example.smarthouse.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class CustomRoomTypeResponse {
    @SerializedName("custom_room_type_id")
    private Long customRoomTypeId;

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("name")
    private String name;

    public CustomRoomTypeResponse() {}

    public CustomRoomTypeResponse(Long id, String name, Long userId) {
        this.customRoomTypeId = id;
        this.name = name;
        this.userId = userId;
    }

    public Long getCustomRoomTypeId() { return customRoomTypeId; }
    public void setCustomRoomTypeId(Long customRoomTypeId) { this.customRoomTypeId = customRoomTypeId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
