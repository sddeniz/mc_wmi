package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.behsa.sdp.mc_wmi.common.RateLimitService;
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

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    void init() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
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

        maxBind[] binds = objectMapper.readValue(permissionDto.getMaxBind(), maxBind[].class);
        for (maxBind bind : binds) {
            logger.info(request.getRemoteAddr().trim() + " ip user");
            if (bind.getIp().equals(request.getRemoteAddr().trim()) && !rateLimitService.isValidCountIp(rateKey, bind.getMaxBind())) {
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }


        if (!rateLimitService.getBucket(rateKey).tryConsume(1)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        return true;
    }

    public class maxBind {
        private String ip;
        private int maxBind;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getMaxBind() {
            return maxBind;
        }

        public void setMaxBind(int maxBind) {
            this.maxBind = maxBind;
        }
    }
}