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


@Component
public class ApiGwSyncResponse implements ISdpHandlerAsync {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGwSyncResponse.class);

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
                LOGGER.error("session is null, to trackCode:{}  , jsonIncome:{}", trackCode, jsonObject != null ? jsonObject.toJSONString() : null);
            } else {
                if (session.getServiceType().equals(ServiceTypeEnums.rest)) {
                    responseRest(trackCode, session, jsonObject);
                } else if (session.getServiceType().equals(ServiceTypeEnums.web)) {
                    responseWeb(trackCode, session, jsonObject);
                }

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
            String returnMessage = jsonObject != null ? jsonObject.toJSONString() : new JSONObject().toJSONString();
            LOGGER.error("response to trackCode:{}  , jsonIncome:{}", trackCode, returnMessage);

            String serviceName = null;
            String serviceVersion = null;
            if (session != null) {
                serviceName = session.getServiceName();
                serviceVersion = session.getVersion();
            }
            this.apiLogger.insert(
                    serviceName,
                    trackCode,
                    EventTypeEnums.getFromTree.getValue(),
                    "Trace",
                    serviceVersion,
                    serviceName,
                    "",
                    returnMessage,
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
        TreeInfoDto treeInfoDto = cacheRestAPI.getHashMap(serviceName);//todo edit this.
        LOGGER.debug("----------------------- Response tree:{} ", jsonObject != null ? jsonObject.toJSONString() : null);

        ApiOutputDto[] outPutDto = objectMapper.readValue(treeInfoDto.getOutputs(), ApiOutputDto[].class);
        JSONObject apiJsonObjWrapper = new JSONObject();
        if (outPutDto == null || outPutDto.length == 0) {
            LOGGER.warn("----------------------- Response After Maping ApiOutputDto is null or empty ");
            return apiJsonObjWrapper;
        }
        for (ApiOutputDto output : outPutDto) {
            if (output.getExpose() == null || (output.getExpose().equals("false") && output.getDefaultValue().isEmpty())) {
                break;
            } else if (output.getType().equals(String.valueOf(ActionFieldValueType.Object)) && output.getExpose().equals("true")) {
                apiJsonObjWrapper = jsonObject;
            } else if (output.getExpose().equals("true")) {

                Object os = jsonObject == null ? null : jsonObject.get(output.getName()).toString().replace("$$$##$$$", "="); /*just for url */
                apiJsonObjWrapper.put(output.getTitle(), os);
            } else {
                apiJsonObjWrapper.put(output.getTitle(), output.getDefaultValue());
            }
        }
        LOGGER.debug("----------------------- wrapper for response to user,Obj:{} ", apiJsonObjWrapper);
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
        return webViewModel;

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
            default:
                webViewModel.setBody(value);
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
        session.getWebDeferredResult().setResult(new ModelAndView("templateResponseApi", "view", responseAfterWrap));
        sessionManager.removeSession(trackCode);
    }
}
