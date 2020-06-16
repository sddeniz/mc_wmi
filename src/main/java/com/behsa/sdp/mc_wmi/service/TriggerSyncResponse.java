package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.SessionModel;
import com.google.gson.Gson;
import models.SdpMsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sdpMsSdk.ISdpHandlerAsync;

@Component
public class TriggerSyncResponse implements ISdpHandlerAsync {

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Gson gson;


    @Override
    public void OnReceive(JSONObject jsonObject, String s, JSONObject jsonObject1, String s1, long l, String s2) throws SdpMsException {
        try {
            System.out.println(jsonObject);
            SessionModel session = sessionManager.getSession(s);
            jsonObject.remove("sdp_userId");
            jsonObject.put("iotel_trackCode", s);
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(jsonObject, HttpStatus.OK);
            session.getDeferredResult().setResult(jsonObjectResponseEntity);
            sessionManager.removeSession(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
