package com.behsa.sdp.mc_wmi.dto;

import java.io.Serializable;

public class BillingResponseDto implements Serializable {

    private  String responseCode;
    private String responseDesc;

    public BillingResponseDto() {
    }

    public BillingResponseDto(String responseCode, String responseDesc) {
        this.responseCode = responseCode;
        this.responseDesc = responseDesc;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

    @Override
    public String toString() {
        return "BillingResponseDTO{" +
                "responseCode='" + responseCode + '\'' +
                ", responseDesc='" + responseDesc + '\'' +
                '}';
    }
}
