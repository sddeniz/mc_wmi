package com.behsa.sdp.mc_wmi.common;

import com.behsa.sdp.mc_wmi.utils.AppConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
public class ConnectionProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConnectionProvider.class);
    private HikariDataSource dataSource;

    private final AppConfig config;

    private static ConnectionProvider INSTANCE;

    public ConnectionProvider(AppConfig config) {
        this.config = config;
    }


    public Connection getConnection() throws Exception {
        try {
            dataSource = config.createHikariConnection();
            return this.dataSource.getConnection();
        } catch (Exception ex) {
            LOGGER.error("getConnection", ex);
            try {
                Thread.sleep(config.connectionErrorDelayTime());
            } catch (Exception e) {
                LOGGER.error("getConnection2", e);
            }
            throw new Exception("Error in get connection", ex);
        }
    }
}
