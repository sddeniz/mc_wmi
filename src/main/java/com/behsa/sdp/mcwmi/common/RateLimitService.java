package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.redis.CoreRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RateLimitService {
    @Autowired
    private CoreRedis coreRedis;


    public Long getTpsTpd(String key) {
         return this.coreRedis.getRateLimitTpsTpd(key);
    }


    public long addTps(String key) {
        Long rateLimitBacketTest = this.coreRedis.getRateLimitTpsTpd(key);
        if (rateLimitBacketTest != null && rateLimitBacketTest != 0) {
            throw new DspdWebApiException("Bucket with key " + key + " exist");
        }

        coreRedis.setRateLimitTps(key, 0l);
        return 0l;
    }

    public long addTpd(String key) {
        Long rateLimitBacketTest = this.coreRedis.getRateLimitTpsTpd(key);
        if (rateLimitBacketTest != null && rateLimitBacketTest != 0) {
            throw new DspdWebApiException("Bucket with key " + key + " exist");
        }
        coreRedis.setRateLimitTpd(key, 0l);
        return 0l;
    }

    public boolean checkAndUpdateLimitation(String keyTps, String keyTpd, long tps, long tpd, long maxTpd, long maxTps) {
        tps = tps + 1;
        tpd = tpd + 1;
        if (tps > maxTps || tpd > maxTpd) {
            return false;
        }

        coreRedis.setRateLimitTpsTpd(keyTps,keyTpd,tps,tpd);
        return true;
    }





}
