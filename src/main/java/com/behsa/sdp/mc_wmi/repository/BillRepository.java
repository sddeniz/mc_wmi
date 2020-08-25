package com.behsa.sdp.mc_wmi.repository;


import com.behsa.sdp.mc_wmi.common.ConnectionProvider;
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
     * @param serviceTitle gateway title
     * @return
     */
    public boolean billingValidation(String serviceTitle) {
        try {
            try (Connection connection = connectionProvider.getConnection();
                 CallableStatement stmt = connection.prepareCall
                         ("{call USSD_MIGRATE_BIZ_ts.pkg_billing_dsdp.prc_get_status_account_gateway(?,?,?,?)}")
            ) {
                stmt.setString(1, serviceTitle);
                stmt.registerOutParameter(2, oracle.jdbc.OracleTypes.NUMBER);
                stmt.registerOutParameter(3, oracle.jdbc.OracleTypes.NUMBER);
                stmt.registerOutParameter(4, oracle.jdbc.OracleTypes.VARCHAR);
                stmt.executeQuery();

                return stmt.getInt(3) != 0;
            }

        } catch (Exception e) {
            LOGGER.error("Exception in BankRepository", e);
            return false;
        }
    }

}


