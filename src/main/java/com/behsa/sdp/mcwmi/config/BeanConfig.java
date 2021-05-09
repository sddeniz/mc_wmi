package com.behsa.sdp.mcwmi.config;

import com.behsa.sdp.mcwmi.redis.RedisUserDetailsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanConfig.class);

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

    @Autowired
    private RedisUserDetailsService redisUserDetailsService;

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
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    @Bean
    public AmqpHelper getAmqpHelper() {
        AmqpHelper amqpHelper = new AmqpHelper(rabbitMqHost, rabbitMqPort, rabbitMqUsername, rabbitMqPassword);
        try {
            amqpHelper.declareExchange(logExchange, "direct");
        } catch (IOException e) {
            LOGGER.error("can not declare logger exchange");
         }
        return amqpHelper;
    }

    @Bean
    public void loadUserPermissions() {
        redisUserDetailsService.loadAllUserPermission();
    }


}
