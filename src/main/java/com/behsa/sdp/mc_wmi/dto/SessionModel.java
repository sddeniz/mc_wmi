package com.behsa.sdp.mc_wmi.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public class SessionModel {
    private DeferredResult<ResponseEntity<?>> deferredResult;
    private String metaData;

    public SessionModel(DeferredResult<ResponseEntity<?>> deferredResult, String metaData) {
        this.deferredResult = deferredResult;
        this.metaData = metaData;
    }

    public DeferredResult<ResponseEntity<?>> getDeferredResult() {
        return deferredResult;
    }

    public String getMetaData() {
        return metaData;
    }
}
