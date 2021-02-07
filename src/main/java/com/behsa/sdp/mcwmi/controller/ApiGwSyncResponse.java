package com.behsa.sdp.mcwmi.controller;

import Enums.ActionFieldValueType;
import com.behsa.sdp.mcwmi.common.CacheRestAPI;
import com.behsa.sdp.mcwmi.common.SessionManager;
import com.behsa.sdp.mcwmi.dto.ApiOutputDto;
import com.behsa.sdp.mcwmi.dto.SessionDto;
import com.behsa.sdp.mcwmi.dto.TreeInfoDto;
import com.behsa.sdp.mcwmi.enums.EventTypeEnums;
import com.behsa.sdp.mcwmi.enums.ServiceTypeEnums;
import com.behsa.sdp.mcwmi.log.APILogger;
import com.behsa.sdp.mcwmi.repository.WebViewModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import sdpMsSdk.ISdpHandlerAsync;

import javax.annotation.PostConstruct;
import java.util.Date;

//import models.SdpMsException;

@Component
public class ApiGwSyncResponse implements ISdpHandlerAsync {
    private final static Logger LOGGER = LoggerFactory.getLogger(ApiGwSyncResponse.class);

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
            session = this.sessionManager.getSession(trackCode);
            if (session == null) {
                LOGGER.error("session is null, to trackCode:{}  , jsonIncome:{}", trackCode, jsonObject.toJSONString());
            } else {
                if (session.getServiceType().equals(ServiceTypeEnums.rest)) {
                    responseRest(trackCode, session, jsonObject);
                } else if (session.getServiceType().equals(ServiceTypeEnums.web)) {
                    responseWeb(trackCode, session, jsonObject);
                }


//            SessionWebViewDto sessionMap = sessionWebViewManager.getSessionMap(trackCode);
//            String responseAfterWrap = wrapperOutPutWebView(sessionMap.getServiceName(), jsonObject);
//
//            String view = "paymentSuccess";
//
//
//            sessionMap.getDeferredResult().setResult(new ModelAndView(view, "model", responseAfterWrap));
//            sessionWebViewManager.removeSession(trackCode);
//
            /* session = sessionManager.getSession(trackCode);
             JSONObject responseAfterWrap = wrapperOutPut(session.getServiceName(), jsonObject);
            ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(responseAfterWrap, HttpStatus.OK);

            responseAfterWrap.put("ApiGw_Code", trackCode);
            responseAfterWrap.put("httpStatus", jsonObjectResponseEntity.getStatusCode());
            responseAfterWrap.put("httpStatusCode", jsonObjectResponseEntity.getStatusCodeValue());

            session.getDeferredResult().setResult(jsonObjectResponseEntity);
            sessionManager.removeSession(trackCode);
            LOGGER.debug("response to trackCode:{}  , status:{}", trackCode, jsonObjectResponseEntity.getStatusCode());*/

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
            }

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
            } else if (output.getType().equals(String.valueOf(ActionFieldValueType.Object)) && output.getExpose().equals("true")) {
                apiJsonObjWrapper = jsonObject;
            } else if (output.getExpose().equals("true")) {
                apiJsonObjWrapper.put(output.getTitle(), jsonObject.get(output.getName()));
            } else {
                apiJsonObjWrapper.put(output.getTitle(), output.getDefaultValue());
            }
        }
        return apiJsonObjWrapper;
    }

    //   -----------------

    private WebViewModel wrapperOutPutWebView(String serviceName, JSONObject jsonObject) throws JsonProcessingException {
        TreeInfoDto treeInfoDto = cacheRestAPI.getHashMap(serviceName);//todo edit this
        ApiOutputDto[] outPutDto = objectMapper.readValue(treeInfoDto.getOutputs(), ApiOutputDto[].class);
        WebViewModel webViewModel = new WebViewModel();
        if (outPutDto == null || outPutDto.length == 0) {
            return webViewModel;
        }

        for (ApiOutputDto output : outPutDto) {
            if (output.getExpose() == null || (output.getExpose().equals("false") && output.getDefaultValue().isEmpty())) {
                break;
            } else if (output.getExpose().equals("true")) {
                Object data = jsonObject.get(output.getName());
                String value = (String) ((LinkedTreeMap) data).get(output.getTitle());
                setModelProperty(webViewModel, output.getTitle(), value);
            } else {
                setModelProperty(webViewModel, output.getTitle(), output.getDefaultValue());
            }
        }
        return webViewModel;//apiJsonObjWrapper;

    }

    private void setModelProperty(WebViewModel webViewModel, String output, String value) {
        switch (output) {
            case "header":
                webViewModel.setHeader(value);
                break;
            case "body":
                webViewModel.setBody(value);
                break;
            case "footer":
                webViewModel.setFooter(value);
                break;
            case "file":
                webViewModel.setFile(value);
                break;
        }

    }

    private void responseRest(String trackCode, SessionDto session, JSONObject jsonObject) throws JsonProcessingException {

        JSONObject responseAfterWrap = wrapperOutPut(session.getServiceName(), jsonObject);
        ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(responseAfterWrap, HttpStatus.OK);

        responseAfterWrap.put("ApiGw_Code", trackCode);
        responseAfterWrap.put("httpStatus", jsonObjectResponseEntity.getStatusCode());
        responseAfterWrap.put("httpStatusCode", jsonObjectResponseEntity.getStatusCodeValue());

        session.getRestDeferredResult().setResult(jsonObjectResponseEntity);
        sessionManager.removeSession(trackCode);
        LOGGER.debug("response to trackCode:{}  , status:{}", trackCode, jsonObjectResponseEntity.getStatusCode());
    }

    private void responseWeb(String trackCode, SessionDto session, JSONObject jsonObject) throws JsonProcessingException {
        WebViewModel responseAfterWrap = wrapperOutPutWebView(session.getServiceName(), jsonObject);
        session.getWebDeferredResult().setResult(new ModelAndView("templateResponseApi", "view",responseAfterWrap));
        sessionManager.removeSession(trackCode);
    }
}
