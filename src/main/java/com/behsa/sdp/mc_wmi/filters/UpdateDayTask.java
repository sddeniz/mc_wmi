package com.behsa.sdp.mc_wmi.filters;

import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * update date per day
 */

public class UpdateDayTask extends TimerTask {
    private LimitManager limitManager;

    public UpdateDayTask(LimitManager limitManager) {

        this.limitManager = limitManager;
    }

    @Override
    public void run() {
        limitManager.setCurrentDate();
    }
}
