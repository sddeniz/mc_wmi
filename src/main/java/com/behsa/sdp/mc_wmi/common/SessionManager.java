package com.behsa.sdp.mc_wmi.common;

import com.behsa.sdp.mc_wmi.dto.SessionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionManager {
    Map<String, SessionModel> sessionMap;

    @PostConstruct
    public void initialize() {
        sessionMap = new HashMap<>();
    }

    public void setSession(String trackCode, SessionModel result) {
        sessionMap.put(trackCode, result);
    }

    public SessionModel getSession(String trackCode) {
        return sessionMap.get(trackCode);
    }

    public void removeSession(String trackCode){
        sessionMap.remove(trackCode);
    }
}
