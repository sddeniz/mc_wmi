package com.behsa.sdp.mcwmi.common;

import com.behsa.sdp.mcwmi.dto.TreeInfoDto;
import com.behsa.sdp.mcwmi.repository.RestApiRepository;
import common.CoreException;
import models.Recipe;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * validation for input from rest service(client)  and define  in RestAPI service DSDP system
 */
@Component
public class ValidationInputService {
    @Autowired
    RestApiRepository restApiRepository;

    private JSONObject restApiInputs = new JSONObject();


    public boolean isValidInputs(JSONObject payload) throws CoreException {
        List<Recipe> restApiRepository = this.restApiRepository.getRestApiRepository();


        if (restApiRepository.size() > payload.size())
            return false;

        else {
            payload.get(restApiRepository.get(0));
        }
        return true;
    }

    //if return null mean dont have tree or have error
    public boolean isHaveTree(TreeInfoDto treeInfoDto) {
        return treeInfoDto != null;
    }
}
