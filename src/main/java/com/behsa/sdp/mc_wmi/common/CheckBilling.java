package com.behsa.sdp.mc_wmi.common;

import com.behsa.sdp.mc_wmi.dto.BillingResponseDto;
import com.behsa.sdp.mc_wmi.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

@Component
public class CheckBilling extends TimerTask {
    private String gwTitle;

    @Autowired
    Timer timer;

    @Autowired
    private BillRepository billRepository;

    public String getGwTitle() {
        return gwTitle;
    }

    public void setGwTitle(String gwTitle) {
        this.gwTitle = gwTitle;
    }

    @Override
    public void run() {
        fetchResultBilling();
    }

    private BillingResponseDto fetchResultBilling() {
        return this.billRepository.getBillResult(gwTitle);
    }

    public String billingCheck(Long periodMilliSecond, CheckBilling checkBilling) {

        BillingResponseDto billingResponseDTO = fetchResultBilling();
        if (periodMilliSecond != null) {
            System.out.println("Run BankCacheManager thread");
            timer.schedule(checkBilling, 0, periodMilliSecond);
        }
        return billingResponseDTO.getResponseCode();
    }

}
