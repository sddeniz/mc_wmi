package com.behsa.sdp.mc_wmi.dto;

import com.behsa.sdp.mc_wmi.enums.ServiceTypeEnums;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * ساخت دیفر ( منتظر پاسخ از سیستم میباشد )
 */
public class SessionWebViewDto {
    private DeferredResult<ModelAndView> deferredResult;
    private String version;
    private String serviceName;
    private ServiceTypeEnums serviceType;

    public SessionWebViewDto(DeferredResult<ModelAndView> deferredResult, String serviceName, String version , ServiceTypeEnums serviceType) {
        this.deferredResult = deferredResult;
        this.version = version;
        this.serviceName = serviceName;
    }

    public DeferredResult<ModelAndView> getDeferredResult() {
        return deferredResult;
    }

    public void setDeferredResult(DeferredResult<ModelAndView> deferredResult) {
        this.deferredResult = deferredResult;
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
}
