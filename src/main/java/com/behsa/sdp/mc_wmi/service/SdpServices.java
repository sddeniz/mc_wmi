package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sdpMsSdk.SdpHelper;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class SdpServices {

    @Autowired
    private SdpHelper sdpHelper;
    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private TriggerSyncResponse triggerSyncResponse;
    @Autowired
    private ContinueResponse continueResponse;

    @PostConstruct
    private void initialize() {
        try {
            sdpHelper.startConsumeAsync("sdp_crm/api_response/"+serviceUtils.getServiceInstanceKey(), 1, true, triggerSyncResponse);
            sdpHelper.startConsumeAsync("sdp_crm/api_continued_response/"+serviceUtils.getServiceInstanceKey(), 1, true, continueResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
