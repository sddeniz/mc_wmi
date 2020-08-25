package com.behsa.sdp.mc_wmi.dto;

import java.io.Serializable;

public class ApiOutputDto implements Serializable {

    private String title;
    private String name;
    private String type;
    private String defaultValue;
    private String destination;
    private String expose;


    public ApiOutputDto() {
    }

    public ApiOutputDto(String title, String name, String type, String defaultValue, String destination, String expose) {
        this.title = title;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.destination = destination;
        this.expose = expose;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getExpose() {
        return expose;
    }

    public void setExpose(String expose) {
        this.expose = expose;
    }


    @Override
    public String toString() {
        return "ApiOutputDto{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", distination='" + destination + '\'' +
                ", expose='" + expose + '\'' +
                '}';
    }
}
