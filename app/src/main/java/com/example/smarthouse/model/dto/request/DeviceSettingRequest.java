package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class DeviceSettingRequest {
    @SerializedName("device_id")
    private Long deviceId;

    @SerializedName("parameter")
    private String parameter;

    @SerializedName("value")
    private String value;

    public DeviceSettingRequest(Long deviceId, String parameter, String value) {
        this.deviceId = deviceId;
        this.parameter = parameter;
        this.value = value;
    }
}
