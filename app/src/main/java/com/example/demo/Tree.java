package com.example.demo;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class Tree {
    private int id;
    private int imageTree;
    private String name;
    private String state;
    private String description;
    private int temperature;
    private int humidity;

    public Tree(int image, String name, String state, String description, int temperature, int humidity) {
        this.imageTree = image;
        this.name = name;
        this.state = state;
        this.description = description;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageTree() {
        return imageTree;
    }

    public void setImageTree(int imageTree) {
        this.imageTree = imageTree;
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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}

