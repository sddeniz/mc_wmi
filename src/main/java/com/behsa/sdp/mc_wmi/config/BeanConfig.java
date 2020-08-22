package com.behsa.sdp.mc_wmi.config;

import com.google.gson.Gson;
import common.CoreException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import sdpMsSdk.AmqpHelper;
import sdpMsSdk.SdpHelper;

import java.io.IOException;

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

    @Value("${sdp.Ip}")
    public String Ip;

    @Value("${sdp.log.exchange}")
    private String logExchange;


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
    public JedisPool initializeRedis() {
        if (redisPassword.isEmpty()) {
            return new JedisPool(new JedisPoolConfig(), redisHost, redisPort
                    , Protocol.DEFAULT_TIMEOUT);
        }
        return new JedisPool(new JedisPoolConfig(), redisHost, redisPort
                , Protocol.DEFAULT_TIMEOUT, redisPassword);
    }

    @Bean
    public Gson getGson() {
        return new Gson();
    }

    @Bean
    public AmqpHelper getAmqpHelper() {
        AmqpHelper amqpHelper = new AmqpHelper(rabbitMqHost, rabbitMqPort, rabbitMqUsername, rabbitMqPassword);
        try {
            amqpHelper.declareExchange(logExchange, "direct");
        } catch (IOException e) {
            System.out.println("can not declare logger exchange");
            e.printStackTrace();
        }
        return amqpHelper;
    }


}
