package com.behsa.sdp.mc_wmi.common;

import org.springframework.security.core.AuthenticationException;

public class DsdpAuthException extends AuthenticationException {
    public DsdpAuthException(String msg) {
        super(msg);
    }
}
