package com.behsa.sdp.mc_wmi.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * ساخت دیفر ( منتظر پاسخ از سیستم میباشد )
 */
public class SessionDto {
    private DeferredResult<ResponseEntity<?>> deferredResult;
    private boolean expectResponse;

    public SessionDto(DeferredResult<ResponseEntity<?>> deferredResult) {
        this.deferredResult = deferredResult;
    }

    public DeferredResult<ResponseEntity<?>> getDeferredResult() {
        return deferredResult;
    }

    public void setDeferredResult(DeferredResult<ResponseEntity<?>> deferredResult) {
        this.deferredResult = deferredResult;
    }

    public boolean isExpectResponse() {
        return expectResponse;
    }

    public void setExpectResponse(boolean expectResponse) {
        this.expectResponse = expectResponse;
    }
}