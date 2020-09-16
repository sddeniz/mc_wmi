package com.behsa.sdp.mc_wmi.log;

import com.behsa.sdp.mc_wmi.utils.AppConfig;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sdpMsSdk.AmqpHelper;

@Component
public class APILogger {

    private final static Logger logger = LoggerFactory.getLogger(APILogger.class);
    private final Object lock = new Object();

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private Gson gson;

    @Autowired
    private AmqpHelper amqpHelper;

    public void insert(String serviceName, String trackCode, String eventType, String level,
                       String sessionId,
                       String phoneNo,
                       String ussdCode,
                       String returnedMessage,
                       String sdpAppletId,
                       String sdpCardCode,
                       String keepAlive,
                       String elapsed,
                       String errorCode,
                       String execLog) {

        LogModel logModel = new LogModel(serviceName, trackCode, eventType,
                level, sessionId, phoneNo,
                ussdCode, returnedMessage, sdpAppletId,
                sdpCardCode, keepAlive, elapsed
                , errorCode, execLog);
        send(logModel);
    }


    private void send(LogModel logModel) {
        try {
            String str = this.gson.toJson(logModel);
            byte[] body = str.getBytes();
            synchronized (lock) {
                amqpHelper.publish(this.appConfig.logExchange, this.appConfig.logRoutingKey, body);
            }
        } catch (Exception e) {
            logger.error("Error in send log" + this.gson.toJson(logModel), e);
        }
    }
}
