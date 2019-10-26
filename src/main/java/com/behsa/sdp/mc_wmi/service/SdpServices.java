package com.behsa.sdp.mc_wmi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sdpMsSdk.SdpHelper;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class SdpServices {

    @Autowired
    SdpHelper sdpHelper;

    @Autowired
    private SdpResponseHandler sdpResponseHandler;

    @PostConstruct
    private void initialize() {
        try {
            sdpHelper.startConsumeAsync("wmi/wmi_response", 1, false, sdpResponseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
