package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.ServiceUtils;
import com.behsa.sdp.mc_wmi.controller.ApiGwSyncResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sdpMsSdk.SdpHelper;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class RestApiResponseServices {

    @Autowired
    private SdpHelper sdpHelper;
    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private ApiGwSyncResponse apiGwSyncResponse;

    @PostConstruct
    private void initialize() {
        try {

            sdpHelper.startConsumeAsync("sdp_api/RestEnd/" + serviceUtils.getServiceInstanceKey(), 1, true, apiGwSyncResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
