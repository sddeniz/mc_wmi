package com.behsa.sdp.mc_wmi.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {



    @Value("${sdp.db.host}")
    private String dbHost;
    @Value("${sdp.db.port}")
    private Integer dbPort;
    @Value("${sdp.db.serviceName}")
    private String dbServiceName;
    @Value("${sdp.db.username}")
    private String dbUserName;
    @Value("${sdp.db.password}")
    private String dbPassword;

    @Value("${sdp.hikari.connectionError.delay}")
    private Integer dbConnErrorDelayMills;

    @Value("${sdp.hikari.minIdle}")
    private Integer hikariMinIdle;
    @Value("${sdp.hikari.maxPollSize}")
    private Integer hikariMaxPollSize;
    @Value("${sdp.hikari.leakDetectionThreshold}")
    private Long hikariLeakDetectionThreshold;
    @Value("${sdp.hikari.maxLifetime}")
    private Long hikariMaxLifeTime;
    @Value("${sdp.hikari.idle.timeOut}")
    private Long hikariIdleTimeout;
    @Value("${sdp.hikari.timeOut}")
    private Long hikariConnectionTimeout;


    @Value("${sdp.rabbitmq.host}")
    private String rabbitHost;
    @Value("${sdp.rabbitmq.port}")
    private Integer rabbitPort;
    @Value("${sdp.rabbitmq.username}")
    private String rabbitUserName;
    @Value("${sdp.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${sdp.redis.host}")
    private String redisHost;
    @Value("${sdp.redis.port}")
    private Integer redisPort;
    @Value("${sdp.redis.password}")
    private String redisPassword;

    @Value("${sdp.log.exchange}")
    public String logExchange;
    @Value("${sdp.log.RoutingKey}")
    public String logRoutingKey;
    @Value("${sdp.log.SystemActive}")
    private Boolean logSystemActive;
    @Value("${sdp.log.debugMode}")
    private Boolean debugMode;

    @Value("${sdp.end.prefetch}")
    public String sdp_api_prefetch;


    @Bean
    public HikariDataSource createHikariConnection() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMinimumIdle(hikariMinIdle);
        hikariConfig.setMaximumPoolSize(hikariMaxPollSize);
        hikariConfig.setUsername(dbUserName);
        hikariConfig.setPassword(dbPassword);
        Long leakDetectionThreshold = hikariLeakDetectionThreshold;
        Long hikariMaxLifeTime = this.hikariMaxLifeTime;
        hikariConfig.setLeakDetectionThreshold(leakDetectionThreshold);
        hikariConfig.setMaxLifetime(hikariMaxLifeTime);
        hikariConfig.setJdbcUrl("jdbc:oracle:thin:@" + dbHost + ":" + dbPort + "/" + dbServiceName);
        hikariConfig.setDriverClassName("oracle.jdbc.OracleDriver");
        hikariConfig.setIdleTimeout(hikariIdleTimeout);
        hikariConfig.setConnectionTimeout(hikariConnectionTimeout);
        return new HikariDataSource(hikariConfig);
    }


    @Bean
    public long connectionErrorDelayTime() {
        return Long.parseLong(String.valueOf(dbConnErrorDelayMills));
    }
}
