package com.behsa.sdp.mc_wmi.common;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitService {
    private final Map<String, RateLimitBacket> bucketMap = new ConcurrentHashMap<>();
    private final Map<String, Long> ipRateMap = new ConcurrentHashMap<>();

    private AtomicLong currentIpSize = new AtomicLong(0);


    public boolean existBuckets(String key) {
        return bucketMap.containsKey(key);
    }

    public RateLimitBacket getBucket(String key) {
        return this.bucketMap.get(key);
    }

    public void addBucket(String key, Long maxTps, Long maxTpd) {
        if (bucketMap.containsKey(key)) {
            throw new DspdWebApiException("Bucket with key " + key + " exist");
        }
        Refill tpsRefill = Refill.greedy(maxTps, Duration.ofSeconds(1));
        Bandwidth tpsLimit = Bandwidth.classic(maxTps, tpsRefill);
        Bucket tpsBucket = Bucket4j.builder().addLimit(tpsLimit).build();

        Refill tpdRefill = Refill.greedy(maxTpd, Duration.ofDays(1));
        Bandwidth tpdLimit = Bandwidth.classic(maxTpd, tpdRefill);
        Bucket tpdBucket = Bucket4j.builder().addLimit(tpdLimit).build();


        RateLimitBacket backets = new RateLimitBacket(tpsBucket, tpdBucket);
        bucketMap.put(key, backets);
    }


    public boolean isValidCountIp(String key, long maxBind) {
        if (ipRateMap.get(key) == null) {
            ipRateMap.put(key, currentIpSize.get());
            return true;

        } else if (currentIpSize.get() > maxBind) {
            return false;
        } else {
            ipRateMap.put(key, currentIpSize.decrementAndGet());
            return true;
        }
    }

    public class RateLimitBacket {
        /**
         * transaction per second
         */
        private Bucket tps;
        /**
         * transaction per day
         */
        private Bucket tpd;

        public RateLimitBacket(Bucket tps, Bucket tpd) {
            this.tps = tps;
            this.tpd = tpd;

        }

        public Bucket getTps() {
            return tps;
        }
        public Bucket getTpd() {
            return tpd;
        }

        public boolean tryConsume(long numTokens) {
            return this.tpd.tryConsume(numTokens) &&
                    this.tps.tryConsume(numTokens);

        }
    }
}
