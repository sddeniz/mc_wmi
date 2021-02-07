package com.behsa.sdp.mcwmi.enums;

public enum EventTypeEnums {
    sendToWorker("requestApiGw"),
    getFromTree("responseApiGw");

    private String value;

    EventTypeEnums(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
