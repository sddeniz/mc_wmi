package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.MetaData;
import com.behsa.sdp.mc_wmi.dto.MetaDataInOut;
import com.behsa.sdp.mc_wmi.dto.MetaDataOutput;
import com.behsa.sdp.mc_wmi.dto.SessionModel;
import com.google.gson.Gson;
import models.SdpMsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sdpMsSdk.ISdpHandlerAsync;

import java.util.Date;

@Component
public class SdpResponseHandler implements ISdpHandlerAsync {

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Gson gson;

    @Override
    public void OnReceive(JSONObject jsonObject, String s, JSONObject jsonObject1, String s1, long l, String s2) throws SdpMsException {
        try {
            System.out.println(jsonObject);
            JSONObject res = new JSONObject();
            SessionModel session = sessionManager.getSession(s);
            String metaDataStr = jsonObject.get("metadata").toString();
            MetaData metaData = gson.fromJson(metaDataStr, MetaData.class);
            for (MetaDataOutput output : metaData.getOutputs()) {
                String valueStr = jsonObject.get(output.getApiName()).toString();

                createOutput(res, output.getApiName(), valueStr, output.getType());
            }
            for (MetaDataInOut inOut : metaData.getInouts()) {
                String valueStr = jsonObject.get(inOut.getApiName()).toString();

                createOutput(res, inOut.getApiName(), valueStr, inOut.getType());
            }
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(res, HttpStatus.OK);
            session.getDeferredResult().setResult(jsonObjectResponseEntity);
            sessionManager.removeSession(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createOutput(JSONObject res, String apiName, String valueStr, String type) {
        switch (type) {
            case "DATE":
                if (valueStr == null || "".equals(valueStr)) {
                    res.put(apiName, null);
                } else {
                    Date date = new Date(Long.parseLong(valueStr));
                    res.put(apiName, date);
                }
                break;
            case "NUMERIC":
                if (valueStr == null || "".equals(valueStr)) {
                    res.put(apiName, null);
                } else {
                    long longVal = Long.parseLong(valueStr.contains(".") ? valueStr.substring(0, valueStr.indexOf('.')) : valueStr);
                    res.put(apiName, longVal);
                }
                break;
            case "VARCHAR":
                res.put(apiName, valueStr);
                break;
            case "CLOB":
                res.put(apiName, valueStr);
                break;
            case "BLOB":
                res.put(apiName, valueStr);
                break;
        }
    }
}
