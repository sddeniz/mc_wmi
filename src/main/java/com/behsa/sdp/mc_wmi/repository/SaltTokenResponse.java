package com.behsa.sdp.mc_wmi.repository;

import java.io.Serializable;

public class SaltTokenResponse implements Serializable {


    private final String saltToken;

    public SaltTokenResponse(String token) {
        this.saltToken = token;
    }

    public String getToken() {
        return this.saltToken;
    }

}
