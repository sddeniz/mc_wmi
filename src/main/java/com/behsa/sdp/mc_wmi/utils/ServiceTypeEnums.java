package com.behsa.sdp.mc_wmi.utils;

public enum ServiceTypeEnums {
    rest(1, "Rest"),
    ussd(2, "USSD");


    private int code;
    private String value;

    ServiceTypeEnums(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
