package com.behsa.sdp.mc_wmi.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PermissionDto {

    private Long id;
    private String userName;
    private String serviceTitle;
    private Long tps;
    private Long tpd;
    private String startDatePermission;
    private String endDatePermission;
    private Map<String, Integer> maxBind;
    private String serviceTimeOut;
    private Long userId;
    private Long serviceId;

    public PermissionDto(Long id, String userName, String serviceTitle, Long tps, Long tpd, String startDatePermission, String endDatePermission, List<MaxBind> maxBind, String serviceTimeOut, Long userId, Long serviceId) {
        this.id = id;
        this.userName = userName;
        this.serviceTitle = serviceTitle;
        this.tps = tps;
        this.tpd = tpd;
        this.startDatePermission = startDatePermission;
        this.endDatePermission = endDatePermission;
        this.maxBind = maxBind.stream().collect(Collectors.toMap(MaxBind::getIp, MaxBind::getMaxBind));
        this.serviceTimeOut = serviceTimeOut;
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getTps() {
        return tps;
    }

    public void setTps(Long tps) {
        this.tps = tps;
    }

    public Long getTpd() {
        return tpd;
    }

    public void setTpd(Long tpd) {
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

    public Map<String, Integer> getMaxBind() {
        return maxBind;
    }

    public void setMaxBind(Map<String, Integer> maxBind) {
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
