package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.CacheRestAPI;
import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.ApiOutputDto;
import com.behsa.sdp.mc_wmi.dto.SessionDto;
import com.behsa.sdp.mc_wmi.dto.TreeInfoDto;
import com.behsa.sdp.mc_wmi.enums.EventTypeEnums;
import com.behsa.sdp.mc_wmi.log.APILogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sdpMsSdk.ISdpHandlerAsync;

import javax.annotation.PostConstruct;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    void init() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }


    @Override
    public void OnReceive(JSONObject jsonObject, String trackCode, JSONObject jsonObject1, String s1, long l, String s2) {
        long startTime = new Date().getTime();
        SessionDto session = null;
        try {
            session = sessionManager.getSession(trackCode);
            JSONObject responseAfterWrap = wrapperOutPut(session.getServiceName(), jsonObject);
            responseAfterWrap.put("DSDP_Code", trackCode);
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(responseAfterWrap, HttpStatus.OK);
            responseAfterWrap.put("httpStatus", jsonObjectResponseEntity.getStatusCode());
            responseAfterWrap.put("httpStatusCode", jsonObjectResponseEntity.getStatusCodeValue());
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

    /**
     * wrapper for out put result of tree
     *
     * @param serviceName
     * @param jsonObject
     * @return
     * @throws JsonProcessingException
     */
    private JSONObject wrapperOutPut(String serviceName, JSONObject jsonObject) throws JsonProcessingException {
        TreeInfoDto treeInfoDto = cacheRestAPI.getHashMap(serviceName);//todo edit this
        ApiOutputDto[] outPutDto = objectMapper.readValue(treeInfoDto.getOutputs(), ApiOutputDto[].class);
        JSONObject apiJsonObjWrapper = new JSONObject();
         if (outPutDto == null || outPutDto.length == 0) {
            return apiJsonObjWrapper;
        }
        for (ApiOutputDto output : outPutDto) {
             if (output.getExpose() == null || (output.getExpose().equals("false") && output.getDefaultValue().isEmpty())) {
                break;
            } else if (output.getExpose().equals("true")) {
                apiJsonObjWrapper.put(output.getTitle(), jsonObject.get(output.getName()));
            } else {
                apiJsonObjWrapper.put(output.getTitle(), output.getDefaultValue());
            }
        }

        return apiJsonObjWrapper;


    }
}
