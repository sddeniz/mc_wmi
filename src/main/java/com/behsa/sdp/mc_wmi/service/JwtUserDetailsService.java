package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.repository.IUserRepository;
import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import com.behsa.sdp.mc_wmi.repository.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@EnableJpaRepositories
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private RestApiRepository restApiRepository;

    @Autowired
    IUserRepository iUserRepository;


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel byUserName = iUserRepository.findByUserName(username);


        if (byUserName == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Map<String, PermissionDto> permission = restApiRepository.getPermission(username);
        return new User(username, byUserName.getPasswords(), permission.values());
    }
}
