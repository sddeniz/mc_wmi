package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.ServiceUtils;
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
    private TriggerSyncResponse triggerSyncResponse;

    @PostConstruct
    private void initialize() {
        try {
            sdpHelper.startConsumeAsync("sdp_api/RestEnd/" + serviceUtils.getServiceInstanceKey(), 1, true, triggerSyncResponse);//todo change to rest API
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
