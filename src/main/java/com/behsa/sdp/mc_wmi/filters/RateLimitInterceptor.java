package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.behsa.sdp.mc_wmi.common.RateLimitService;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);

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
        if (!rateLimitService.existBuckets(rateKey)) {
            PermissionDto permissionDto = null;
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
        return true;
    }
}