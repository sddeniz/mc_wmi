package com.behsa.sdp.mcwmi.controller;


import com.behsa.sdp.mcwmi.common.DsdpAuthentication;
import com.behsa.sdp.mcwmi.common.DsdpUser;
import com.behsa.sdp.mcwmi.dto.JwtRequest;
import com.behsa.sdp.mcwmi.dto.JwtResponse;
import com.behsa.sdp.mcwmi.dto.SaltTokenResponse;
import com.behsa.sdp.mcwmi.entity.UserModel;
import com.behsa.sdp.mcwmi.utils.Constants;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import com.behsa.sdp.mcwmi.redis.CoreRedis;
import com.behsa.sdp.mcwmi.redis.RedisUserDetailsService;
import common.EncryptUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationController.class);

    @Autowired
    private RedisUserDetailsService userDetailsService;

    @Autowired
    private CoreRedis coreRedis;

    private Map<String, UserModel> userPassMap = new HashMap<>();


    private UserModel loadUserByCache(String userName, String pass) {

        if (userPassMap.isEmpty()) {
            List<UserModel> allUsersForCache = userDetailsService.findAllUsersForCache();
            userPassMap = allUsersForCache.stream().collect(Collectors.toMap(UserModel::getUserName,
                    Function.identity()));
        }
        UserModel userModel = userPassMap.get(userName);
        if (userModel != null && userModel.getPasswords() != null && userModel.getPasswords().equals(pass)) {
            return userModel;
        }
        LOGGER.info("** Can not load , user:{} , pass:{}", userName, pass);
        UserModel user = userDetailsService.checkUserPass(userName, pass);
        userPassMap.put(user.getUserName(), user);
        return user;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {

            String username = authenticationRequest.getUsername();
            String password = pass(authenticationRequest.getPassword());

            UserModel user = loadUserByCache(username, password);
            final DsdpUser userDetails = userDetailsService.checkAndLoadUser(username, user.getPasswords());
            DsdpAuthentication authentication = new DsdpAuthentication(userDetails, null, userDetails.getPermissions(), Collections.emptyList());
            final String token = UUID.randomUUID().toString();
            coreRedis.setAuthentication(token, authentication);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            LOGGER.error("authenticate error,", e);
//    todo        return ResponseEntity.badRequest();
        }
        return null;
    }

    private synchronized String pass(String password) throws Exception {
        return EncryptUtil.encrypt(password);

    }


    @RequestMapping(value = "/serviceToken", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationServiceToken(@RequestBody(required = true) JSONObject payload, HttpServletRequest request) {
        try {

            List<String> inputServiceList = (List<String>) payload.get("services");
            if (inputServiceList.isEmpty()) {
                LOGGER.warn("input list is empty");
                return ResponseEntity.ok(new JwtResponse(""));
            }

            final String authToken = request.getHeader(Constants.AuthenticationHeader);
            DsdpAuthentication authentication = this.coreRedis.getAuthentication(authToken);
            if (authentication == null) {
                LOGGER.warn("serviceToken response from redis,authentication is null ");
                return ResponseEntity.ok(new SaltTokenResponse("Error"));
            }
            Map<String, PermissionDto> permissions = authentication.getPermissions();
            LOGGER.info("service Token Permissons, size:{}", permissions.size());
            Map<String, PermissionDto> servicePermissions = new ConcurrentHashMap<>();
            for (String inputServices : inputServiceList) {
                if (permissions.get(inputServices) != null) {
                    servicePermissions.put(inputServices, permissions.get(inputServices));
                }
            }
            authentication.setPermissions(servicePermissions);
            final String serviceToken = UUID.randomUUID().toString();
            LOGGER.info("---------- service token , permission acc for service token, serviceToken:{}, size:{} ", serviceToken, servicePermissions.size());
            coreRedis.setAuthenticationSaltToken(serviceToken, authentication);
            return ResponseEntity.ok(new SaltTokenResponse(serviceToken));
        } catch (Exception zx) {
            LOGGER.error("Error in serviceToken", zx);
            zx.printStackTrace();
        }
        return null;
    }


    @RequestMapping(value = "/authenticate/clearRedis", method = RequestMethod.GET)
    public void cachePermissions() {
        coreRedis.cleanRedis();
    }

}
