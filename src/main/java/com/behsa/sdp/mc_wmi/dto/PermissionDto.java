package com.behsa.sdp.mc_wmi.dto;

import java.io.Serializable;


public class PermissionDto implements Serializable {

    private String userName;
    private String serviceTitle;
    private String tps;
    private String tpd;
    private String startDatePermission;
    private String endDatePermission;
    private String maxBind;
    private String serviceTimeOut;
    private Long userId;
    private Long serviceId;

    public PermissionDto(String userName, String serviceTitle, String tps, String tpd, String startDatePermission, String endDatePermission, String maxBind, String serviceTimeOut, Long userId, Long serviceId) {
        this.userName = userName;
        this.serviceTitle = serviceTitle;
        this.tps = tps;
        this.tpd = tpd;
        this.startDatePermission = startDatePermission;
        this.endDatePermission = endDatePermission;
        this.maxBind = maxBind;
        this.serviceTimeOut = serviceTimeOut;
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getTps() {
        return tps;
    }

    public void setTps(String tps) {
        this.tps = tps;
    }

    public String getTpd() {
        return tpd;
    }

    public void setTpd(String tpd) {
        this.tpd = tpd;
    }

    public String getStartDatePermission() {
        return startDatePermission;
    }

    public void setStartDatePermission(String startDatePermission) {
        this.startDatePermission = startDatePermission;
    }

    public String getEndDatePermission() {
        return endDatePermission;
    }

    public void setEndDatePermission(String endDatePermission) {
        this.endDatePermission = endDatePermission;
    }

    public String getMaxBind() {
        return maxBind;
    }

    public void setMaxBind(String maxBind) {
        this.maxBind = maxBind;
    }

    public String getServiceTimeOut() {
        return serviceTimeOut;
    }

    public void setServiceTimeOut(String serviceTimeOut) {
        this.serviceTimeOut = serviceTimeOut;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "PermissionDto{" +
                "userName='" + userName + '\'' +
                ", serviceTitle='" + serviceTitle + '\'' +
                ", tps='" + tps + '\'' +
                ", tpd='" + tpd + '\'' +
                ", startDatePermission='" + startDatePermission + '\'' +
                ", endDatePermission='" + endDatePermission + '\'' +
                ", maxBind='" + maxBind + '\'' +
                ", serviceTimeOut='" + serviceTimeOut + '\'' +
                ", userId='" + userId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }
}
