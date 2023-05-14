package com.example.demo;

import com.google.gson.annotations.SerializedName;

public class Tree {
    @SerializedName("id")
    private int id;
    @SerializedName("urlimage")
    private String urlimage;
    @SerializedName("name")
    private String name;
    @SerializedName("state")
    private String state;
    @SerializedName("description")
    private String description;
    @SerializedName("humidity")
    private int humidity;

    public Tree(int id, String image, String name, String state, String description, int humidity) {
        this.id = id;
        this.urlimage = image;
        this.name = name;
        this.state = state;
        this.description = description;
        this.humidity = humidity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageTree() {
        return urlimage;
    }

    public void setImageTree(String imageTree) {
        this.urlimage = imageTree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}

