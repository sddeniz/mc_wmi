package com.behsa.sdp.mc_wmi.controller;


import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.behsa.sdp.mc_wmi.common.DsdpUser;
import com.behsa.sdp.mc_wmi.redis.CoreRedis;
import com.behsa.sdp.mc_wmi.redis.RedisUserDetailsService;
import com.behsa.sdp.mc_wmi.repository.JwtRequest;
import com.behsa.sdp.mc_wmi.repository.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private RedisUserDetailsService userDetailsService;

    @Autowired
    private CoreRedis coreRedis;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            final DsdpUser userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

            DsdpAuthentication authentication = new DsdpAuthentication(userDetails, null, userDetails.getPermissions(), Collections.emptyList());
            final String token = UUID.randomUUID().toString();
            coreRedis.setAuthentication(token, authentication);
//            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
//            return ResponseEntity.ok(token);
        } catch (Exception zx) {

            zx.printStackTrace();
        }
        return null;
    }

}
