package com.example.smarthouse.model;

public class DeviceAddingModel {
    private int imgRes;
    private String name;

    public DeviceAddingModel(int imgRes, String name) {
        this.imgRes = imgRes;
        this.name = name;
    }

    public int getImgRes() { return imgRes; }
    public String getName() { return name; }
}