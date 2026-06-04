package com.example.smarthouse.model.dto.request;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ChangeDeviceSettingsRequest {
        @SerializedName("deviceParameters")
        private Map<String, String> deviceParameters;

        public ChangeDeviceSettingsRequest(Map<String, String> deviceParameters) {
                this.deviceParameters = deviceParameters;
        }

        public Map<String, String> getDeviceParameters() {
                return deviceParameters;
        }
}