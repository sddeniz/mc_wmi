package com.behsa.sdp.mc_wmi.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ValueCastHelper {
    public <T> T cast(Object value, String typeStr) {
        switch (typeStr){
            case "DATE":
                return (T) Date.class.cast(value);
        }
        return null;
    }
}
