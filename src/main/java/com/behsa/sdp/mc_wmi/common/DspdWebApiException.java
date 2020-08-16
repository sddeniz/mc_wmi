package com.behsa.sdp.mc_wmi.common;

public class DspdWebApiException extends RuntimeException {
    public DspdWebApiException(String message) {
        super(message);
    }

    public DspdWebApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
