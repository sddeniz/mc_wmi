package com.behsa.sdp.mcwmi.repository;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WebViewModel implements Serializable {

    @SerializedName("body")
    private String body;
    @SerializedName("title")
    private String title;
    @SerializedName("header")
    private String header;
    @SerializedName("footer")
    private String footer;
    @SerializedName("file")
    private String file;


    public WebViewModel() {
    }

    public WebViewModel(String body, String title, String header, String footer, String file) {
        this.body = body;
        this.title = title;
        this.header = header;
        this.footer = footer;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public WebViewModel(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
