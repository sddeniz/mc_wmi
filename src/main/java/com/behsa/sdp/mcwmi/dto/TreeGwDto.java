package com.behsa.sdp.mcwmi.dto;

import java.io.Serializable;

public class TreeGwDto implements Serializable {
    private String title;
    private Integer type;
    private String currentVersion;
    private String version; //restApi version
    private String domain; //domain name


    public TreeGwDto(String title, Integer type, String currentVersion, String version, String domain) {
        this.title = title;
        this.type = type;
        this.currentVersion = currentVersion;
        this.version = version;
        this.domain = domain;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "TreeGwDto{" +
                "title='" + title + '\'' +
                ", type=" + type +
                ", currentVersion='" + currentVersion + '\'' +
                ", version='" + version + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}

