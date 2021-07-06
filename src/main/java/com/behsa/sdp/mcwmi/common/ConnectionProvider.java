package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.config.DataBaseConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
public class ConnectionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProvider.class);
    private final DataBaseConfig config;
    private HikariDataSource dataSource;


    public ConnectionProvider(DataBaseConfig config) {
        this.config = config;
        this.dataSource = config.createHikariConnection();
    }


    public Connection getConnection() throws Exception {
        try {
            return dataSource.getConnection();
        } catch (Exception ex) {
            LOGGER.error("getConnection", ex);
            Thread.sleep(config.connectionErrorDelayTime());
            throw new Exception("Error in get connection", ex);
        }
    }
}
