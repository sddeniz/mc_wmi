package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.SessionModel;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sdpMsSdk.ISdpHandlerAsync;

//import models.SdpMsException;

@Component
public class ContinueResponse implements ISdpHandlerAsync {

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Gson gson;


    @Override
    public void OnReceive(JSONObject jsonObject, String s, JSONObject jsonObject1, String s1, long l, String s2) {
        try {
            System.out.println("resssssssssssponse ");
            System.out.println(jsonObject);
            SessionModel session = sessionManager.getSession(s);
            jsonObject.remove("sdp_userId");
            jsonObject.put("DSDP_Code", s);
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(jsonObject, HttpStatus.OK);
            jsonObject.put("httpStatus", jsonObjectResponseEntity.getStatusCode());
            jsonObject.put("httpStatusCode", jsonObjectResponseEntity.getStatusCodeValue());

            session.getDeferredResult().setResult(jsonObjectResponseEntity);
            session.setExpectResponse(true);
            sessionManager.setSession(s, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
