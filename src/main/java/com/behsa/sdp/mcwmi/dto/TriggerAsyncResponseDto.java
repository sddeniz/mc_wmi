package com.behsa.sdp.mcwmi.dto;

import org.json.simple.JSONObject;

public class TriggerAsyncResponseDto {
    private String type;
    private String trackingCode;
    private JSONObject payload;

    public TriggerAsyncResponseDto(String type, String trackingCode, JSONObject payload) {
        this.type = type;
        this.trackingCode = trackingCode;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }
}
