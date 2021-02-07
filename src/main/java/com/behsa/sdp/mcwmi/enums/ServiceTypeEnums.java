package com.behsa.sdp.mcwmi.enums;

public enum ServiceTypeEnums {
    rest(1, "RestApiGw"),
    soap(2, "SoapApiGw"),
    web(3, "WebView"),
    ussd(4, "USSD"),
    notDefine(-1, "-");


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

    public static ServiceTypeEnums getEnum(int code) {
        for (ServiceTypeEnums e : values()) {
            if (e.getCode() == (code))
                return e;
        }
        return notDefine;
    }

}
