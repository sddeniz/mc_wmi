package com.behsa.sdp.mc_wmi.common;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitService {
    private final Map<String, RateLimitBacket> bucketMap = new ConcurrentHashMap<>();

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
        Refill refill = Refill.greedy(maxTps, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(maxTps, refill);
        Bucket tpsBucket = Bucket4j.builder().addLimit(limit).build();

        Refill refill2 = Refill.greedy(maxTpd, Duration.ofDays(1));
        Bandwidth limit2 = Bandwidth.classic(maxTpd, refill2);
        Bucket tpdBucket = Bucket4j.builder().addLimit(limit2).build();

        RateLimitBacket backets = new RateLimitBacket(tpsBucket, tpdBucket);
        bucketMap.put(key, backets);
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
            return this.tpd.tryConsume(numTokens) && this.tps.tryConsume(numTokens);
        }
    }
}
