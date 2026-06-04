package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class UpdateDeviceSettingsRequest {
    @SerializedName("deviceParameters")
    private Map<String, String> parameters;

    public UpdateDeviceSettingsRequest(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}