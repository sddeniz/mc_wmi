package com.behsa.sdp.mcwmi.redis;

import com.behsa.sdp.mcwmi.common.DsdpUser;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import com.behsa.sdp.mcwmi.repository.IUserRepository;
import com.behsa.sdp.mcwmi.repository.RestApiRepository;
import com.behsa.sdp.mcwmi.repository.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@EnableJpaRepositories
public class RedisUserDetailsService { //,UserDetailsService {

    @Autowired
    private RestApiRepository restApiRepository;

    @Autowired
    private IUserRepository iUserRepository;


    public DsdpUser checkAndLoadUser(String username, String password) {
        UserModel user = checkUserPass(username, password);
        Map<String, PermissionDto> permission = restApiRepository.getPermission(username);
        return new DsdpUser(username, user.getPasswords(), permission);
    }

    private UserModel checkUserPass(String username, String password) {
        UserModel user = iUserRepository.findUserModelByUserNameAndPasswords(username, password);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;

    }

 /*   @Override
    public DsdpUser loadUserByUsername(String username) {

        UserModel byUserName = iUserRepository.findByUserName(username);
        if (byUserName == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Map<String, PermissionDto> permission = restApiRepository.getPermission(username);
        return new DsdpUser(username, byUserName.getPasswords(), permission);
    }*/
}
