package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.dto.TreeInfoDto;
import org.springframework.stereotype.Component;

/**
 * validation for input from rest service(client)  and define  in RestAPI service DSDP system
 */
@Component
public class ValidationInputService {

    //if return null mean dont have tree or have error
    public boolean isHaveTree(TreeInfoDto treeInfoDto) {
        return treeInfoDto != null;
    }
}
