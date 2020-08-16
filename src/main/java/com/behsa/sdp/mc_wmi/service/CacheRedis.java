package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Timer;

@Component
public class CacheRedis extends Timer {

    @Autowired
    private CoreRedis coreRedis;

    @Autowired
    private RestApiRepository restApiRepository;

    @Autowired
    private Timer timer ;


    /**
     * insert into redis if whith key is null
     *  @param username
     * @param serviceName
     * @param permissionRule
     * @return
     */
    private PermissionDto checkRedis(String username, String serviceName, PermissionDto permissionRule) {
        PermissionDto permissionDto = coreRedis.readCacheRedis(username + serviceName);
        if (permissionDto == null) {
            coreRedis.insertCacheRedis(username + serviceName, permissionRule);
        }
        return permissionDto;
    }

//    public  checkUserNameRedis(String username )
//    {
//
//    }


    /**
     * fill all permission that need
     *
     * @param userName username
     * @return all permision list for this user
     */
//    private List<PermissionDto> fillPermissionList(String userName) {
//        List<PermissionDto> permission = restApiRepository.getPermission(userName);
//        for (PermissionDto permissionDto : permission) {
//            checkRedis(permissionDto.getUserName(), permissionDto.getServiceTitle(), permissionDto);
//        }
//
//        return permission;
//    }



}
