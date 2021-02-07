package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.dto.SessionDto;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionManager {
    Map<String, SessionDto> sessionMap;

    @PostConstruct
    public void initialize() {
        sessionMap = new HashMap<>();
    }

    public void setSession(String trackCode, SessionDto result) {
        sessionMap.put(trackCode, result);
    }

    public SessionDto getSession(String trackCode) {
        return sessionMap.get(trackCode);
    }

    public void removeSession(String trackCode){
        sessionMap.remove(trackCode);
    }

}
