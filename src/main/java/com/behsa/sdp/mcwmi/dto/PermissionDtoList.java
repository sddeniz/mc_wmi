package com.behsa.sdp.mcwmi.dto;

 import java.io.Serializable;
 import java.util.Map;

public class PermissionDtoList implements Serializable {
     private  Map<String, Map<String, PermissionDto>> userMapPermissionMap;

    public PermissionDtoList(Map<String, Map<String, PermissionDto>> userMapPermissionMap) {
        this.userMapPermissionMap = userMapPermissionMap;
    }

    public Map<String, Map<String, PermissionDto>> getUserMapPermissionMap() {
        return userMapPermissionMap;
    }

    public void setUserMapPermissionMap(Map<String, Map<String, PermissionDto>> userMapPermissionMap) {
        this.userMapPermissionMap = userMapPermissionMap;
    }
}
