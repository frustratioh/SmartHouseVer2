package com.example.smarthouse.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class RoomTypeResponse {
    @SerializedName("room_type_id")
    private Long roomTypeId;

    @SerializedName("name")
    private String name;

    @SerializedName("image_url")
    private String imageUrl;

    public RoomTypeResponse() {}

    public RoomTypeResponse(Long id, String name, String imageUrl) {
        this.roomTypeId = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Long getRoomTypeId() { return roomTypeId; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
