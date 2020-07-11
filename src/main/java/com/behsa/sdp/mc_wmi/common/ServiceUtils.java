package com.behsa.sdp.mc_wmi.common;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * ساختن کد یکتا
 */
@Component
public class ServiceUtils {
    private String serviceInstanceKey;

    @PostConstruct
    private void initServiceUtils(){
        serviceInstanceKey = UUID.randomUUID().toString();
    }

    public String getServiceInstanceKey(){
        return this.serviceInstanceKey;
    }
}
