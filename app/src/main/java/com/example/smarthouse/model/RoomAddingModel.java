package com.example.smarthouse.model;

public class RoomAddingModel {
    private int imgRes;
    private String name;

    public RoomAddingModel(int imgRes, String name) {
        this.imgRes = imgRes;
        this.name = name;
    }

    public int getImgRes() { return imgRes; }
    public String getName() { return name; }
}