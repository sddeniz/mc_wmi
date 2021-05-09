package com.behsa.sdp.mcwmi.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@Component
public class Utils implements Serializable {

    @Value("${remoteHeader}")
    private String remoteHeader;


    public String returnIp(HttpServletRequest request) {
        for (String header : remoteHeader.split(",")) {
            String userRemoteIp = request.getHeader(header);
            if (userRemoteIp != null && userRemoteIp.length() != 0 && !"unknown".equalsIgnoreCase(userRemoteIp)) {
                return userRemoteIp;
            }
        }
        return request.getRemoteAddr().trim();
    }
}
