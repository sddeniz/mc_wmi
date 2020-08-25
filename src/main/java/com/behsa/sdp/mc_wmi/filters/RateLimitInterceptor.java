package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.behsa.sdp.mc_wmi.common.RateLimitService;
import com.behsa.sdp.mc_wmi.dto.MaxBind;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private ConcurrentHashMap<String, Long> maxBindMap = new ConcurrentHashMap<>();
    private MaxBind maxBind;


    @PostConstruct
    void init() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        maxBind = new MaxBind();
    }


    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof DsdpAuthentication)) {
            return true;
        }
        String pathVariables = request.getRequestURI();
        String serviceName = pathVariables.replace("/api/call/", "");

        if (serviceName.isEmpty()) {
            logger.error("FORBIDDEN");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
            return false;
        }
        UserDetails details = (UserDetails) authentication.getPrincipal();
        String rateKey = serviceName + "." + details.getUsername();
        PermissionDto permissionDto = null;
        if (!rateLimitService.existBuckets(rateKey)) {
            for (GrantedAuthority authority : details.getAuthorities()) {
                if (authority.getAuthority().equals(serviceName)) {
                    permissionDto = (PermissionDto) authority;
                    break;
                }
            }
            if (permissionDto == null) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return false;
            }
            rateLimitService.addBucket(rateKey, permissionDto.getTps(), permissionDto.getTpd());
        }

        if (!rateLimitService.getBucket(rateKey).tryConsume(1)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }

        String requestIp = request.getRemoteAddr().trim();
        String rateBindKey = serviceName + "." + details.getUsername() + "." + requestIp;
        for (GrantedAuthority authority : details.getAuthorities()) {
            permissionDto = (PermissionDto) authority;
            MaxBind[] binds = objectMapper.readValue(permissionDto.getMaxBind(), MaxBind[].class);
            List<MaxBind> maxBindList = Arrays.stream(binds).filter(s -> s.getIp() != null && s.getIp().equals(requestIp)).collect(Collectors.toList());
            if (authority.getAuthority().equals(serviceName)) {
                List<MaxBind> collect = maxBindList.stream().filter(s -> s.getIp().equals(requestIp)).collect(Collectors.toList());
                if (collect.isEmpty()) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                } else {
                    if (!validateRateMaxBind(rateBindKey, collect.get(0).getMaxBind())) {
                        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());//todo change this
                        return false;
                    }
                }
            }
        }


        return true;
    }

    private boolean validateRateMaxBind(String key, int maxBind) {
        if (maxBindMap.get(key) == null) {
            maxBindMap.put(key, 1L);
            return true;
        }
        Long maxBindCount = maxBindMap.get(key);
        if (maxBindCount > maxBind)
            return false;
        maxBindMap.put(key, maxBindCount + 1);
        return true;
    }


}