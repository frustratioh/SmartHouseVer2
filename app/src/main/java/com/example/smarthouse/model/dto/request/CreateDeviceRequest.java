package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class CreateDeviceRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("unique_id")
    private String uniqueId;

    @SerializedName("device_type_id")
    private Long deviceTypeId;

    @SerializedName("room_id")
    private Long roomId;

    public CreateDeviceRequest(String name, String uniqueId, Long deviceTypeId, Long roomId) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.deviceTypeId = deviceTypeId;
        this.roomId = roomId;
    }
}
