package com.behsa.sdp.mc_wmi.service;

import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private RestApiRepository restApiRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("javainuse".equals(username)) {
            Map<String, PermissionDto> permission = restApiRepository.getPermission(username);
            return new User("javainuse", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
                    permission.values());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
