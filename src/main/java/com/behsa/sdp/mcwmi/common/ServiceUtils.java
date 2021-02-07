package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.enums.ServiceTypeEnums;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * ساختن کد یکتا
 */
@Component
public class ServiceUtils {
    private String serviceInstanceKey;

    @PostConstruct
    private void initServiceUtils() {
        serviceInstanceKey = UUID.randomUUID().toString();
    }

    public String getServiceInstanceKey() {
        return this.serviceInstanceKey;
    }

    public static String findServiceNameAndType(HttpServletRequest request) {
        String apiType = "";
        String serviceName = "";
        if (request.getRequestURI().contains("/api/call")) {
            serviceName = request.getRequestURI().replace("/api/call/", "");
            apiType = ServiceTypeEnums.rest.getValue();
        } else if (request.getRequestURI().contains("/web/call")) {
            serviceName = request.getRequestURI().replace("/web/call/", "");
            apiType = ServiceTypeEnums.web.getValue();
        }

        return apiType + serviceName;
    }
}
