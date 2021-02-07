package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.dto.PermissionDto;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Map;

public class DsdpUser extends User {
    private final Map<String, PermissionDto> permissions;

    public DsdpUser(String username, String password, Map<String, PermissionDto> permissions) {
        super(username, password, new ArrayList<>());
        this.permissions = permissions;
    }

    public Map<String, PermissionDto> getPermissions() {
        return this.permissions;
    }
}
