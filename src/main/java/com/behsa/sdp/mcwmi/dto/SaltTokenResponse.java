package com.behsa.sdp.mcwmi.dto;

import java.io.Serializable;

public class SaltTokenResponse implements Serializable {


    private String serviceToken;

    public SaltTokenResponse() {
    }

    public SaltTokenResponse(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    public String getServiceToken() {
        return serviceToken;
    }

    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }
}
