package com.example.smarthouse.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class DeviceSettingResponse {
    @SerializedName("id")
    private Long deviceSettingId;

    @SerializedName("device_id")
    private Long deviceId;

    @SerializedName("parameter")
    private String parameter;

    @SerializedName("value")
    private String value;

    public Long getDeviceSettingId() { return deviceSettingId; }
    public Long getDeviceId() { return deviceId; }
    public String getParameter() { return parameter; }
    public String getValue() { return value; }
}
