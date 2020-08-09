package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.common.ServiceUtils;
import com.behsa.sdp.mc_wmi.utils.AppConfig;
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
    @Autowired
    private AppConfig appConfig;
//    @Autowired
//    private ResponseEndHandler responseEndHandler;


    @PostConstruct
    private void initialize() {

        try {
            System.out.println("que res :" + "sdp_api/api_response/" + serviceUtils.getServiceInstanceKey());
            sdpHelper.startConsumeAsync("sdp_api/api_response/" + serviceUtils.getServiceInstanceKey(), 1, true, triggerSyncResponse);//todo change to rest API
            // sdpHelper.startConsumeAsync("sdp_api/api_continued_response/"+serviceUtils.getServiceInstanceKey(), 1, true, continueResponse);//todo change to rest API
//            sdpHelper.startConsumeSync("sdp_api/response_to_user_End" + "/" + serviceUtils.getServiceInstanceKey(),
//                    Integer.parseInt(appConfig.sdp_api_prefetch), false, 30, false
//                    , new SdpReponseEndHandler(ussdCommand, sessionManager, logger, cacheHelper));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
