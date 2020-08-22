package com.behsa.sdp.mc_wmi.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * ساخت دیفر ( منتظر پاسخ از سیستم میباشد )
 */
public class SessionDto {
    private DeferredResult<ResponseEntity<?>> deferredResult;
    private String version;
    private String serviceName;

    public SessionDto(DeferredResult<ResponseEntity<?>> deferredResult, String serviceName, String version) {
        this.deferredResult = deferredResult;
        this.serviceName = serviceName;
        this.version = version;
    }

    public DeferredResult<ResponseEntity<?>> getDeferredResult() {
        return deferredResult;
    }

    public void setDeferredResult(DeferredResult<ResponseEntity<?>> deferredResult) {
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
}
