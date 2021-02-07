package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.dto.TreeInfoDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CacheRestAPI {

    private HashMap<String, TreeInfoDto> hashMap;


    public CacheRestAPI() {
        this.hashMap = new HashMap<>();
    }

    public TreeInfoDto getHashMap(String gwTitle) {
        return hashMap.get(gwTitle);
    }

    public void setHashMap(String gwTitle, TreeInfoDto treeInfoDto) {
        hashMap.put(gwTitle, treeInfoDto);
    }

}
