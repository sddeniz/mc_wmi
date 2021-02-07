package com.behsa.sdp.mcwmi.dto;

import com.behsa.sdp.mcwmi.enums.ServiceTypeEnums;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * ساخت دیفر ( منتظر پاسخ از سیستم میباشد )
 */
public class SessionDto {
    private DeferredResult<ResponseEntity<?>> RestDeferredResult;
    private String version;
    private String serviceName;
    private ServiceTypeEnums serviceType;
    private DeferredResult<ModelAndView> webDeferredResult;

//    public SessionDto(DeferredResult<ResponseEntity<?>> deferredResult, String serviceName, String version) {
//        this.deferredResult = deferredResult;
//        this.serviceName = serviceName;
//        this.version = version;
//    }

    public SessionDto(DeferredResult<ResponseEntity<?>> RestDeferredResult, DeferredResult<ModelAndView> webDeferredResult, String serviceName, String version, ServiceTypeEnums serviceType) {
        this.RestDeferredResult = RestDeferredResult;
        this.version = version;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.webDeferredResult=webDeferredResult;
    }

    public DeferredResult<ResponseEntity<?>> getRestDeferredResult() {
        return RestDeferredResult;
    }

    public void setRestDeferredResult(DeferredResult<ResponseEntity<?>> restDeferredResult) {
        this.RestDeferredResult = restDeferredResult;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ServiceTypeEnums getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeEnums serviceType) {
        this.serviceType = serviceType;
    }

    public DeferredResult<ModelAndView> getWebDeferredResult() {
        return webDeferredResult;
    }

    public void setWebDeferredResult(DeferredResult<ModelAndView> webDeferredResult) {
        this.webDeferredResult = webDeferredResult;
    }
}
