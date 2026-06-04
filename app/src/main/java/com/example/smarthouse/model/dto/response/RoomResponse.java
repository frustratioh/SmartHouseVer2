package com.example.smarthouse.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class RoomResponse {
    @SerializedName("id")
    private Long roomId;

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("room_type_id")
    private Long roomTypeId;

    @SerializedName("custom_room_type_id")
    private Long customRoomTypeId;

    @SerializedName("name")
    private String name;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(Long roomTypeId) { this.roomTypeId = roomTypeId; }
    public Long getCustomRoomTypeId() { return customRoomTypeId; }
    public void setCustomRoomTypeId(Long customRoomTypeId) { this.customRoomTypeId = customRoomTypeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isCustom() {
        return customRoomTypeId != null;
    }
}
