package com.behsa.sdp.mc_wmi.dto;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Objects;

public class Authority implements GrantedAuthority, Serializable {
    private final String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority)) return false;
        Authority authority1 = (Authority) o;
        return Objects.equals(authority, authority1.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }
}
