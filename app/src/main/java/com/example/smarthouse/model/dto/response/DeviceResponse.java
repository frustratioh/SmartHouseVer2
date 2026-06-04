package com.example.smarthouse.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class DeviceResponse {
    @SerializedName("device_id")
    private Long deviceId;

    @SerializedName("device_type_id")
    private Long deviceTypeId;

    @SerializedName("room_id")
    private Long roomId;

    @SerializedName("name")
    private String name;

    @SerializedName("unique_id")
    private String uniqueId;

    public Long getDeviceId() { return deviceId; }
    public Long getDeviceTypeId() { return deviceTypeId; }
    public Long getRoomId() { return roomId; }
    public String getName() { return name; }
    public String getUniqueId() { return uniqueId; }
}
