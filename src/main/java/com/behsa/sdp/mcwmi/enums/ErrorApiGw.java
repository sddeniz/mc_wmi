package com.behsa.sdp.mcwmi.enums;

public enum ErrorApiGw {
    serviceWeb("Erorr,Service is wrong or is not Active !"),
    billingWeb("Erorr, Billing lock your Account"),
    versionWeb("Erorr, Dont Insert Your Version !"),
    inputWeb("Erorr,Input service Fields are wrong !");

    private String value;

    ErrorApiGw(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
