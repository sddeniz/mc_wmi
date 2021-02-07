package com.behsa.sdp.mcwmi.log;

import model.MsUssdLogModel;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;


public class LogModel extends MsUssdLogModel {

    @Value("${sdp.Ip}")
    public String IpConfig;


    public LogModel(String serviceName,
                    String trackCode,
                    String eventType,
                    String level,
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
        this.Ip = IpConfig;
        this.GetwayNumber = serviceName;
        this.Level = level;
        this.DateTime = new Date().getTime();
        this.TrackCode = trackCode != null ? trackCode : "";
        this.EventType = eventType;
        this.SessionId = sessionId;
        this.PhoneNo = phoneNo;
        this.UssdCode = ussdCode;
        this.ReturnedMessage = returnedMessage;
        this.SdpAppletId = sdpAppletId;
        this.SdpCardCode = sdpCardCode;
        this.keepAlive = keepAlive;
        this.Elapsed = elapsed;
        this.ErrorCode = errorCode;
        this.ExecLog = execLog;
    }
}
