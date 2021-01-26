package com.behsa.sdp.mc_wmi;

import com.behsa.sdp.mc_wmi.redis.CoreRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class McWmiApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(McWmiApplication.class, args);

    }

}
