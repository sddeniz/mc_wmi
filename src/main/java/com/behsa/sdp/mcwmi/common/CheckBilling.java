package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckBilling {
     @Autowired
    private BillRepository billRepository;

    public boolean billingCheck(String serviceTitle) {
        return this.billRepository.billingValidation(serviceTitle);
    }

}
