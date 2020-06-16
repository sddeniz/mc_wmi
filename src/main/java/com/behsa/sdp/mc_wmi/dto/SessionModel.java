package com.behsa.sdp.mc_wmi.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public class SessionModel {
    private DeferredResult<ResponseEntity<?>> deferredResult;
    private boolean expectResponse;

    public SessionModel(DeferredResult<ResponseEntity<?>> deferredResult) {
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
