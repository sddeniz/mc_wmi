package com.behsa.sdp.mc_wmi.redis;

import com.behsa.sdp.mc_wmi.common.DsdpUser;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.repository.IUserRepository;
import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import com.behsa.sdp.mc_wmi.repository.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@EnableJpaRepositories
public class RedisUserDetailsService implements UserDetailsService {

    @Autowired
    private RestApiRepository restApiRepository;

    @Autowired
    private IUserRepository iUserRepository;

    @Override
    public DsdpUser loadUserByUsername(String username) {

        UserModel byUserName = iUserRepository.findByUserName(username);
        if (byUserName == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Map<String, PermissionDto> permission = restApiRepository.getPermission(username);
        return new DsdpUser(username, byUserName.getPasswords(), permission);
    }
}
