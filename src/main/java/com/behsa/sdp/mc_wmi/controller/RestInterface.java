package com.behsa.sdp.mc_wmi.controller;

import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.MetaData;
import com.behsa.sdp.mc_wmi.dto.SessionModel;
import com.behsa.sdp.mc_wmi.service.MetaDataLoader;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import sdpMsSdk.SdpHelper;

import java.util.UUID;

@RestController
public class RestInterface {

    @Autowired
    private SdpHelper sdpHelper;
    @Autowired
    private MetaDataLoader metaDataLoader;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Gson gson;

    @PostMapping(value = "/api/call/{method}")
    public @ResponseBody
    DeferredResult<ResponseEntity<?>> createApplet(@PathVariable("method") String method, @RequestBody JSONObject inputs) throws Exception {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        String metaData = metaDataLoader.getMetaData(method);
        MetaData metaDataObj = gson.fromJson(metaData, MetaData.class);
        String metaJson = gson.toJson(metaDataObj);
        inputs.put("metadata", metaJson);
        inputs.put("method", method);
        SessionModel sessionModel = new SessionModel(output, metaData);
        String trackCode = UUID.randomUUID().toString();
        sessionManager.setSession(trackCode, sessionModel);
        sdpHelper.sendStartProcess("wmi", "wmi_trigger", null, inputs, null, trackCode);
        return output;
    }
}
