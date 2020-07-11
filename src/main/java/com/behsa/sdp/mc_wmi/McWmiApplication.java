package com.behsa.sdp.mc_wmi;

import com.behsa.sdp.mc_wmi.service.ValueCastHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;
import java.util.Iterator;

@SpringBootApplication
public class McWmiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(McWmiApplication.class, args);
    }

}
