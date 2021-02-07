package com.behsa.sdp.mcwmi.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Service
public class MetaDataLoader {
    public String getMetaData(String methodName) {
        ClassPathResource classPathResource = new ClassPathResource("metadata/" + methodName + ".json");
        try {
            InputStream inputStream = classPathResource.getInputStream();
            return StreamUtils.copyToString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
