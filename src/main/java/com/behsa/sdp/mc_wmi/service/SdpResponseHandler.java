package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.SessionDto;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sdpMsSdk.ISdpHandlerAsync;

@Component
public class SdpResponseHandler implements ISdpHandlerAsync {

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Gson gson;

    @Override
    public void OnReceive(JSONObject jsonObject, String s, JSONObject jsonObject1, String s1, long l, String s2) {
        try {
            System.out.println(jsonObject);
            JSONObject res = new JSONObject();
            SessionDto session = sessionManager.getSession(s);
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(res, HttpStatus.OK);
            session.getRestDeferredResult().setResult(jsonObjectResponseEntity);
            sessionManager.removeSession(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
