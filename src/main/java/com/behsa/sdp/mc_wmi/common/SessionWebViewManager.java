package com.behsa.sdp.mc_wmi.common;

import com.behsa.sdp.mc_wmi.dto.SessionWebViewDto;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionWebViewManager {
    Map<String, SessionWebViewDto> sessionMap;

    @PostConstruct
    public void initialize() {
        sessionMap = new HashMap<>();
    }

    public SessionWebViewDto getSessionMap(String trackCode) {
        return sessionMap.get(trackCode);

    }

    public void setSessionMap(String key, SessionWebViewDto sessionMap) {
        this.sessionMap.put(key, sessionMap);
    }


    public void removeSession(String trackCode) {
        sessionMap.remove(trackCode);
    }
}
