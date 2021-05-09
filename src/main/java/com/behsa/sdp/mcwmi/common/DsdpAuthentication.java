package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.dto.Authority;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import org.springframework.security.core.Authentication;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DsdpAuthentication implements Serializable, Authentication {
    private static final long serialVersionUID = 3781835596190060415L;
    private final DsdpUser principal;
    private List<Authority> authorities;
    private Map<String, PermissionDto> permissions;
    private Object details;
    private Object credentials;
    private boolean authenticated = false;
    private boolean authorized = false;

    public DsdpAuthentication(DsdpUser principal, Object credentials, Map<String, PermissionDto> permissions, List<Authority> authorities) {
        this.principal = principal;
        this.credentials = credentials;
        this.authorities = authorities;
        this.permissions = permissions;
    }

    public void addAuthorities(Authority authority) {
        this.authorities.add(authority);
    }

    public void removeAuthorities(PermissionDto authority) {
        this.authorities.remove(authority);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public DsdpUser getPrincipal() {
        return this.principal;
    }

    public List<Authority> getAuthorities() {
        return this.authorities;
    }

    public Map<String, PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, PermissionDto> permissions) {
        this.permissions = permissions;
    }

    public String getName() {
        return this.getPrincipal().getUsername();
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }
}
