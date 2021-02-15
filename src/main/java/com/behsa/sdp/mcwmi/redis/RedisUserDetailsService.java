package com.behsa.sdp.mcwmi.redis;

import com.behsa.sdp.mcwmi.common.DsdpUser;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import com.behsa.sdp.mcwmi.dto.PermissionDtoList;
import com.behsa.sdp.mcwmi.repository.IUserRepository;
import com.behsa.sdp.mcwmi.repository.RestApiRepository;
import com.behsa.sdp.mcwmi.repository.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@EnableJpaRepositories
public class RedisUserDetailsService { //,UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUserDetailsService.class);

    @Autowired
    private RestApiRepository restApiRepository;

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private CoreRedis coreRedis;


    public DsdpUser checkAndLoadUser(String username, String password) {
//        UserModel user = checkUserPass(username, password);

        PermissionDtoList redisPermission = coreRedis.getAllPermission("UserzPermi$ion");
        Map<String, PermissionDto> permission = redisPermission == null ? null : redisPermission.getUserMapPermissionMap().get(username);
        if (permission == null || permission.isEmpty()) {
            permission = restApiRepository.getPermission(username);
        }
        return new DsdpUser(username, password, permission);
    }

    public UserModel checkUserPass(String username, String password) {
        UserModel user = iUserRepository.findUserModelByUserNameAndPasswords(username, password);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;

    }

    public void loadAllUserPermission() {
        try {
            List<UserModel> users = iUserRepository.findAll();
            Map<String, Map<String, PermissionDto>> dbPermissions = restApiRepository.getAllPermissions(users);
            if (dbPermissions != null) {
                PermissionDtoList permissionDtoList = new PermissionDtoList(dbPermissions);
                coreRedis.setAllPermission("UserzPermi$ion", permissionDtoList);
                LOGGER.info("success to insert to redis ,load all permissions");
            }
        } catch (Exception ex) {
            LOGGER.error("can not load users permissions", ex);
        }
    }

    public List<UserModel> findAllUsersForCache() {
        return iUserRepository.findAll();
    }

}
