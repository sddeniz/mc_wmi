package com.behsa.sdp.mc_wmi.common;

import com.behsa.sdp.mc_wmi.redis.CoreRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RateLimitService {
    @Autowired
    private CoreRedis coreRedis;
//
//    public RateLimitBacket getBucket(String key) {
//        return this.coreRedis.getRateLimitBacket(key);
//    }


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
//        this.coreRedis.setRateLimitTps(keyTps, tps);
//        this.coreRedis.setRateLimitTpd(keyTpd, tpd);

//        coreRedis.clean();
        coreRedis.setRateLimitTpsTpd(keyTps,keyTpd,tps,tpd);
        return true;
    }





//    public RateLimitBacket addBucket(String key, Long maxTps, Long maxTpd) {
//
//
//        RateLimitBacket rateLimitBacket = coreRedis.getRateLimitBacket(key);
//        if (rateLimitBacket != null && rateLimitBacket.getTps() != null && rateLimitBacket.getTpd() != null) {
//            throw new DspdWebApiException("Bucket with key " + key + " exist");
//        }
//        Refill tpsRefill = Refill.greedy(maxTps, Duration.ofSeconds(1));
//        Bandwidth tpsLimit = Bandwidth.classic(maxTps, tpsRefill);
//        Bucket tpsBucket = Bucket4j.builder().addLimit(tpsLimit).build();
//
//        Refill tpdRefill = Refill.greedy(maxTpd, Duration.ofDays(1));
//        Bandwidth tpdLimit = Bandwidth.classic(maxTpd, tpdRefill);
//        Bucket tpdBucket = Bucket4j.builder().addLimit(tpdLimit).build();
//
//
//        RateLimitBacket backets = new RateLimitBacket(tpsBucket, tpdBucket);
//        coreRedis.setRateLimitBacket(key, backets);
//        return backets;
//    }

//
//    public class RateLimitBacket {
//        public RateLimitBacket() {
//        }
//
//        /**
//         * transaction per second
//         */
//        private Bucket tps;
//        /**
//         * transaction per day
//         */
//        private Bucket tpd;
//
//        public RateLimitBacket(Bucket tps, Bucket tpd) {
//            this.tps = tps;
//            this.tpd = tpd;
//
//        }
//
//        public Bucket getTps() {
//            return tps;
//        }
//
//        public void setTps(Bucket tps) {
//            this.tps = tps;
//        }
//
//        public Bucket getTpd() {
//            return tpd;
//        }
//
//        public void setTpd(Bucket tpd) {
//            this.tpd = tpd;
//        }
//
//        public boolean tryConsume(long numTokens) {
//            return this.tpd.tryConsume(numTokens) &&
//                    this.tps.tryConsume(numTokens);
//
//        }
//
//
//    }


}
