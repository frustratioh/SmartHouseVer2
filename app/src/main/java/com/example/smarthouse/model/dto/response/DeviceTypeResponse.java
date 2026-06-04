package com.example.smarthouse.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class DeviceTypeResponse {
    @SerializedName("device_type_id")
    private Long deviceTypeId;

    @SerializedName("name")
    private String name;

    @SerializedName("image_url")
    private String imageUrl;

    public DeviceTypeResponse() {}

    public DeviceTypeResponse(Long id, String name) {
        this.deviceTypeId = id;
        this.name = name;
    }

    public Long getDeviceTypeId() { return deviceTypeId; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
