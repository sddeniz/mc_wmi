package com.behsa.sdp.mc_wmi.controller;

import com.behsa.sdp.mc_wmi.common.ServiceUtils;
import com.behsa.sdp.mc_wmi.common.SessionManager;
import com.behsa.sdp.mc_wmi.dto.SessionModel;
import com.behsa.sdp.mc_wmi.dto.TriggerAsyncReponseModel;
import com.behsa.sdp.mc_wmi.service.MetaDataLoader;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import sdpMsSdk.SdpHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
public class RestInterface {

    private static final String MICROSERVICE_NAME = "ms_rest";
    private static final String TRIGGER_SYNC_SERVICE = "ms_rest";

    @Autowired
    private SdpHelper sdpHelper;
    @Autowired
    private MetaDataLoader metaDataLoader;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Gson gson;
    @Autowired
    private ServiceUtils serviceUtils;
//
//    @PostMapping(value = "/api/call/{method}")
//    public @ResponseBody
//    DeferredResult<ResponseEntity<?>> createApplet(@PathVariable("method") String method, @RequestBody JSONObject inputs, HttpServletRequest request) throws Exception {
//        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
//        inputs.put("method", method);
//        inputs.put("ipAddress", request.getRemoteAddr());
//        inputs.put("channelType", 1);
//        SessionModel sessionModel = new SessionModel(output, null);
//        String trackCode = UUID.randomUUID().toString();
//        sessionManager.setSession(trackCode, sessionModel);
//        sdpHelper.sendStartProcess("wmi", "wmi_trigger", null, inputs, null, trackCode);
//        return output;
//    }

    @PostMapping(value = "/api/call/{method}")
    public @ResponseBody
    DeferredResult<ResponseEntity<?>> triggerSync(@PathVariable("method") String method, @RequestParam("key") String apiKey, @RequestBody JSONObject payload, HttpServletRequest request) throws Exception {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        System.out.println("received");
        payload.put("iotelapi_key_method", method);
        payload.put("", apiKey);
        SessionModel sessionModel = new SessionModel(output);
        String trackCode = UUID.randomUUID().toString();
        sessionManager.setSession(trackCode, sessionModel);
        sdpHelper.sendStartProcess("sdp_crm", "api_request", serviceUtils.getServiceInstanceKey(), payload, null, trackCode);
        System.out.println("request sent, trackCode: " + trackCode);
        return output;
    }

    @PostMapping(value = "/api/response/{trackCode}")
    public @ResponseBody
    DeferredResult<ResponseEntity<?>> responseSync(@PathVariable("trackCode") String trackCode, @RequestParam("key") String apiKey, @RequestBody JSONObject payload, HttpServletRequest request) throws Exception {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        System.out.println("response");
        SessionModel session = sessionManager.getSession(trackCode);
        session.setDeferredResult(output);
        sdpHelper.sendResponse(payload, trackCode);
        System.out.println("service response sent, trackCode: " + trackCode);
        return output;
    }

    @PostMapping(value = "/trigger/{channelName}/{triggerName}")
    public @ResponseBody
    ResponseEntity<TriggerAsyncReponseModel> triggerAsync(@PathVariable("channelName") String channelName, @PathVariable("triggerName") String triggerName,
                                                          @RequestBody JSONObject payload, HttpServletRequest request) throws Exception {
        String trackCode = "";
        try {
            trackCode = sdpHelper.sendStartProcess(channelName, triggerName, null, payload, null);
            if (trackCode == null || trackCode.equals("")) {
                throw new Exception("خطا در ثبت درخواست");
            }
            System.out.println("request sent, trackCode: " + trackCode);
            return new ResponseEntity<>(new TriggerAsyncReponseModel("ORDINARY", trackCode, null),
                    HttpStatus.OK);
        }
        catch (Exception e) {
            JSONObject jo = new JSONObject();
            jo.put("errorCode", 1);
            jo.put("errorMessage", "خطا در ثبت درخواست");
            return new ResponseEntity<>(new TriggerAsyncReponseModel("ERROR", trackCode, jo),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
