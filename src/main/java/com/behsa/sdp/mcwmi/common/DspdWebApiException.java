package com.behsa.sdp.mcwmi.common;

public class DspdWebApiException extends RuntimeException {
    public DspdWebApiException(String message) {
        super(message);
    }

    public DspdWebApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
