package com.behsa.sdp.mc_wmi.repository;


import com.behsa.sdp.mc_wmi.common.ConnectionProvider;
import com.behsa.sdp.mc_wmi.dto.BillingResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;

@Repository
public class BillRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(BillRepository.class);

    @Autowired
    private ConnectionProvider connectionProvider;

    /**
     * package for know we should close ussd and show message to user or not , if response is 0 . its ok but if not 0 we should show message to usser
     *
     * @param gwTitle gateway title
     * @return
     */
    public BillingResponseDto getBillResult(String gwTitle) {


        BillingResponseDto billingRepositoryResponseDTO = new BillingResponseDto();
        try {
            try (Connection connection = connectionProvider.getConnection();
                 CallableStatement stmt = connection.prepareCall
                         ("{call USSD_MIGRATE_BIZ.pkg_billing_dsdp.prc_get_status_account_gateway(?,?,?,?)}")
            ) {
                stmt.setString(1, gwTitle);
                stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.NUMBER);
                stmt.registerOutParameter(3, oracle.jdbc.OracleTypes.NUMBER);
                stmt.registerOutParameter(4, oracle.jdbc.OracleTypes.VARCHAR);
                stmt.executeQuery();

                billingRepositoryResponseDTO.setResponseCode(stmt.getObject(3) == null ? "-1" : stmt.getObject(3).toString());
                billingRepositoryResponseDTO.setResponseDesc(stmt.getString(4));
                if (billingRepositoryResponseDTO.equals("-1"))
                    billingRepositoryResponseDTO.setResponseDesc("به دلیل بروز مشکل فنی،مجدد تلاش فرمایید");
            }

        } catch (Exception e) {
            LOGGER.error("Exception in BankRepository", e);
            billingRepositoryResponseDTO.setResponseCode("-1");
            billingRepositoryResponseDTO.setResponseDesc("عملیات با خطا مواجه شده است");
        }
        return billingRepositoryResponseDTO;
    }

}


