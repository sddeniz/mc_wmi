package com.behsa.sdp.mc_wmi.common;

import com.behsa.sdp.mc_wmi.dto.TreeInfoDto;
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
