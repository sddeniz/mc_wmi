package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.CacheRestAPI;
import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.SessionDto;
import com.behsa.sdp.mc_wmi.enums.EventTypeEnums;
import com.behsa.sdp.mc_wmi.log.APILogger;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sdpMsSdk.ISdpHandlerAsync;

import java.util.Date;

//import models.SdpMsException;

@Component
public class TriggerSyncResponse implements ISdpHandlerAsync {
    private final static Logger LOGGER = LoggerFactory.getLogger(TriggerSyncResponse.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private CacheRestAPI cacheRestAPI;

    @Autowired
    private APILogger apiLogger;


    @Override
    public void OnReceive(JSONObject jsonObject, String trackCode, JSONObject jsonObject1, String s1, long l, String s2) {
        long startTime = new Date().getTime();
        SessionDto session = null;
        try {
            session = sessionManager.getSession(trackCode);
            //todo add wapper 
            jsonObject.remove("sdp_userId");
            jsonObject.put("DSDP_Code", trackCode);
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(jsonObject, HttpStatus.OK);
            jsonObject.put("httpStatus", jsonObjectResponseEntity.getStatusCode());
            jsonObject.put("httpStatusCode", jsonObjectResponseEntity.getStatusCodeValue());
            session.getDeferredResult().setResult(jsonObjectResponseEntity);
            sessionManager.removeSession(trackCode);
            LOGGER.debug("response to trackCode:{}  , status:{}", trackCode, jsonObjectResponseEntity.getStatusCode());

            this.apiLogger.insert(session.getServiceName(),
                    trackCode,
                    EventTypeEnums.getFromTree.getValue(),
                    "Trace",
                    session.getVersion(),
                    session.getServiceName(),
                    "",
                    " _ ",
                    "", "", "true",
                    String.valueOf(new Date().getTime() - startTime), null, null);

        } catch (Exception e) {
            LOGGER.error("response to trackCode:{}  , jsonIncome:{}", trackCode, jsonObject.toJSONString());
            this.apiLogger.insert(
                    session != null ? session.getServiceName() : null,
                    trackCode,
                    EventTypeEnums.getFromTree.getValue(),
                    "Trace",
                    session != null ? session.getVersion() : null,
                    session != null ? session.getServiceName() : null,
                    "",
                    jsonObject.toJSONString(),
                    "", "", "true",
                    String.valueOf(new Date().getTime() - startTime), "90010", e.getMessage());
        }
    }


    private void wrapperOutPut() {
        cacheRestAPI.getHashMap("sadad");//todo edit this
    }
}
