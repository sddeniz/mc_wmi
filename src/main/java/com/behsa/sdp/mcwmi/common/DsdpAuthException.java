package com.behsa.sdp.mcwmi.common;

import org.springframework.security.core.AuthenticationException;

public class DsdpAuthException extends AuthenticationException {
    public DsdpAuthException(String msg) {
        super(msg);
    }
}
