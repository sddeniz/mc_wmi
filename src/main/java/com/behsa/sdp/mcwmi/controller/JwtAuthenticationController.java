package com.behsa.sdp.mcwmi.controller;


import com.behsa.sdp.mcwmi.common.DsdpAuthentication;
import com.behsa.sdp.mcwmi.common.DsdpUser;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import com.behsa.sdp.mcwmi.redis.CoreRedis;
import com.behsa.sdp.mcwmi.redis.RedisUserDetailsService;
import com.behsa.sdp.mcwmi.repository.HeaderKey;
import com.behsa.sdp.mcwmi.repository.JwtRequest;
import com.behsa.sdp.mcwmi.repository.JwtResponse;
import com.behsa.sdp.mcwmi.repository.SaltTokenResponse;
import common.EncryptUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private RedisUserDetailsService userDetailsService;

    @Autowired
    private CoreRedis coreRedis;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
//     coreRedis.cleanRedis();

        try {
            final DsdpUser userDetails = userDetailsService.checkAndLoadUser(authenticationRequest.getUsername(), EncryptUtil.encrypt(authenticationRequest.getPassword()));
            DsdpAuthentication authentication = new DsdpAuthentication(userDetails, null, userDetails.getPermissions(), Collections.emptyList());
            final String token = UUID.randomUUID().toString();
            coreRedis.setAuthentication(token, authentication);

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception zx) {
            zx.printStackTrace();

            //todo
//            return ResponseEntity.badRequest();
        }
        return null;
    }


    @RequestMapping(value = "/serviceToken", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationServiceToken(@RequestBody(required = true) JSONObject payload, HttpServletRequest request) {
        try {

            List<String> inputServiceList = (List<String>) payload.get("services");
            if (inputServiceList.isEmpty()) {
                return ResponseEntity.ok(new JwtResponse(""));
            }

            final String authToken = request.getHeader(HeaderKey.AuthenticationHeader);
            DsdpAuthentication authentication = this.coreRedis.getAuthentication(authToken);
            if (authentication == null) {
                return ResponseEntity.ok(new SaltTokenResponse("Error"));
            }
            Map<String, PermissionDto> permissions = authentication.getPermissions();
            Map<String, PermissionDto> servicePermissions = new ConcurrentHashMap<>();
            for (String inputServices : inputServiceList) {
                if (permissions.get(inputServices) != null) {
                    servicePermissions.put(inputServices, permissions.get(inputServices));
                }
            }
            authentication.setPermissions(servicePermissions);
            final String serviceToken = UUID.randomUUID().toString();
            coreRedis.setAuthenticationSaltToken(serviceToken, authentication);
            return ResponseEntity.ok(new SaltTokenResponse(serviceToken));
        } catch (Exception zx) {
            zx.printStackTrace();
        }
        return null;
    }


}
