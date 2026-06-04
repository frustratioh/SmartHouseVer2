package com.example.smarthouse.model;

public class DeviceModel {
    private int deviceImgRes;
    private int switchImgRes;
    private String name;
    private String deviceType;
    private boolean isOn;

    public DeviceModel(int deviceImgRes, int switchImgRes, String name, String deviceType, boolean isOn) {
        this.deviceImgRes = deviceImgRes;
        this.switchImgRes = switchImgRes;
        this.name = name;
        this.deviceType = deviceType;
        this.isOn = isOn;
    }

    public int getDeviceImgRes() { return deviceImgRes; }
    public int getSwitchImgRes() { return switchImgRes; }
    public String getName() { return name; }
    public String getDeviceType() { return deviceType; }
    public boolean isOn() { return isOn; }
    public void setOn(boolean on) { isOn = on; }
}