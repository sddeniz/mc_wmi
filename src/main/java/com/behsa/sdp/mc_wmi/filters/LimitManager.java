package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class LimitManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestApiRepository.class);


    private int dailyTransactionLimit;
    private int sessionSizeLimit;
    private int perSecondTransactionLimit;

    private AtomicInteger todayTransactionCount = new AtomicInteger(0);
    private LocalDate currentDate = LocalDate.now();
    private AtomicInteger currentSessionSize = new AtomicInteger(0);

    private AtomicLong currentSecond;
    private AtomicInteger perSecondTransactionCount = new AtomicInteger(0);


    public LimitManager(int dailyTransactionLimit, int sessionSizeLimit, int perSecondTransactionLimit) {
        this.dailyTransactionLimit = dailyTransactionLimit;
        this.sessionSizeLimit = sessionSizeLimit;
        this.perSecondTransactionLimit = perSecondTransactionLimit;
        this.currentSecond = new AtomicLong(new Date().getTime() / 1000);
    }

    public void setCurrentDate() {
        LocalDate now = LocalDate.now();
        if (!currentDate.equals(now)) {
            currentDate = now;
            todayTransactionCount = new AtomicInteger(0);
        }
        LOGGER.debug("current day: "+now);

    }

    public boolean checkDailyLimitTransaction() {
        if (this.dailyTransactionLimit == 0) return true;
        if (todayTransactionCount.get() < this.dailyTransactionLimit) {
            todayTransactionCount.incrementAndGet();
            return true;
        } else {
            return false;
        }
    }


    public boolean checkBindLimit() {
        if (this.sessionSizeLimit == 0) return true;
        System.out.println("sessionSize: " + currentSessionSize.get());
        if (currentSessionSize.get() < this.sessionSizeLimit) {
            currentSessionSize.incrementAndGet();
            return true;
        }
        return false;
    }

    public void releaseSession() {
        if (currentSessionSize.get() > 0) {
            currentSessionSize.decrementAndGet();
            LOGGER.debug("session released, size: " + currentSessionSize);
        }
    }

    public boolean checkTransactionPerSecond() {
        if (this.perSecondTransactionLimit == 0) return true;
        long now = new Date().getTime() / 1000;
        if (currentSecond.get() != now) {
            currentSecond.set(now);
            LOGGER.debug("tps: " + perSecondTransactionCount);
            perSecondTransactionCount.set(1);
            return true;
        } else {
            if (perSecondTransactionCount.get() < this.perSecondTransactionLimit) {
                perSecondTransactionCount.incrementAndGet();
                return true;
            }
            LOGGER.debug("tps is more than: " + perSecondTransactionCount);
            return false;
        }

    }
}
