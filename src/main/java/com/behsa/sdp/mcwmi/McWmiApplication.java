package com.behsa.sdp.mcwmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class McWmiApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(McWmiApplication.class);


    public static void main(String[] args) {
        LOGGER.info("ver 1.0.1 ----------- date:99/12/06 ");
        try {
//            ConfigurableApplicationContext context =
            SpringApplication.run(McWmiApplication.class, args);
        } catch (Exception ex) {
            LOGGER.error("main Edge have error", ex);
        }
    }

}
