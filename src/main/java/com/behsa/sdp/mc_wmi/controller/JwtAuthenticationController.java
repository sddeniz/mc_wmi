package com.behsa.sdp.mc_wmi.controller;


import com.behsa.sdp.mc_wmi.config.JwtTokenUtil;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.repository.JwtRequest;
import com.behsa.sdp.mc_wmi.repository.JwtResponse;
import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import com.behsa.sdp.mc_wmi.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private RestApiRepository restApiRepository;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        List<PermissionDto> permissionDtos = fillPermissionList(authenticationRequest.getUsername());//todo will be from redis
        final String token = jwtTokenUtil.generateToken(userDetails, permissionDtos);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


    /**
     * fill all permission that need
     *
     * @param userName username
     * @return all permision list for this user
     */
    private List<PermissionDto> fillPermissionList(String userName) {
        return restApiRepository.getPermission(userName);
    }

}
