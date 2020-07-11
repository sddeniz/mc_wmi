package com.behsa.sdp.mc_wmi.dto;

import java.io.Serializable;

public class ApiInputDto implements Serializable {

    private String title;
    private String name;
    private String type;
    private String defaultValue;
    private String source;
    private String expose;

    public ApiInputDto() {
    }

    public ApiInputDto(String title, String name, String type, String defaultValue, String source, String expose) {
        this.title = title;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.source = source;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExpose() {
        return expose;
    }

    public void setExpose(String expose) {
        this.expose = expose;
    }
}
