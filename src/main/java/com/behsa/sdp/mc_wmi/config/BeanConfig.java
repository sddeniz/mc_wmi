package com.behsa.sdp.mc_wmi.config;

import common.CoreException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sdpMsSdk.SdpHelper;

import java.util.Timer;

/***
 * config for spring
 */
@Configuration
public class BeanConfig {

    @Value("${sdp.rabbitmq.host}")
    private String rabbitMqHost;
    @Value("${sdp.rabbitmq.port}")
    private int rabbitMqPort;
    @Value("${sdp.rabbitmq.username}")
    private String rabbitMqUsername;
    @Value("${sdp.rabbitmq.password}")
    private String rabbitMqPassword;

    @Value("${sdp.redis.host}")
    private String redisHost;
    @Value("${sdp.redis.port}")
    private int redisPort;
    @Value("${sdp.redis.password}")
    private String redisPassword;

    @Value("${sdp.billPeriodTime}")
    public String billPeriodTime;


    @Bean
    public SdpHelper getSdpHelper() {
        try {
            return new SdpHelper(rabbitMqHost, rabbitMqPort, rabbitMqUsername, rabbitMqPassword, "restApi",
                    redisHost, redisPort, redisPassword, 0);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public Timer timer() {
        return new Timer();
    }

}
