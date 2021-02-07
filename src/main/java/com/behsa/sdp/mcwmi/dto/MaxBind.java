package com.behsa.sdp.mcwmi.dto;

import java.io.Serializable;

public class MaxBind implements Serializable {
    private String ip;
    private int maxBind;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getMaxBind() {
        return maxBind;
    }

    public void setMaxBind(int maxBind) {
        this.maxBind = maxBind;
    }

}
