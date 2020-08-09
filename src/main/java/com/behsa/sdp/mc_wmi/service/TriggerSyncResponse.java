package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.CacheRestAPI;
import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.SessionModel;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sdpMsSdk.ISdpHandlerAsync;

//import models.SdpMsException;

@Component
public class TriggerSyncResponse implements ISdpHandlerAsync {
    private final static Logger LOGGER = LoggerFactory.getLogger(TriggerSyncResponse.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private CacheRestAPI cacheRestAPI;


    @Override
    public void OnReceive(JSONObject jsonObject, String s, JSONObject jsonObject1, String s1, long l, String s2) {
        try {
            System.out.println(jsonObject);
            SessionModel session = sessionManager.getSession(s);
            //todo add wapper 
            jsonObject.remove("sdp_userId");
            jsonObject.put("DSDP_Code", s);
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(jsonObject, HttpStatus.OK);
            jsonObject.put("httpStatus", jsonObjectResponseEntity.getStatusCode());
            jsonObject.put("httpStatusCode", jsonObjectResponseEntity.getStatusCodeValue());
            session.getDeferredResult().setResult(jsonObjectResponseEntity);
            sessionManager.removeSession(s);
            LOGGER.debug("response to trackCode:{}  , status:{}", s, jsonObjectResponseEntity.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void wrapperOutPut() {
        cacheRestAPI.getHashMap("sadad");//todo edit this
    }
}
