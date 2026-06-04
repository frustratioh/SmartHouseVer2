package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class CreateRoomRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("room_type_id")
    private Long roomTypeId;

    @SerializedName("custom_room_type_id")
    private Long customRoomTypeId;

    public CreateRoomRequest(String name, Long userId, Long roomTypeId, boolean isCustom) {
        this.name = name;
        this.userId = userId;
        if (isCustom) {
            this.customRoomTypeId = roomTypeId;
            this.roomTypeId = null;
        } else {
            this.roomTypeId = roomTypeId;
            this.customRoomTypeId = null;
        }
    }
}
