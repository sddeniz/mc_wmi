package com.behsa.sdp.mcwmi.filters;

import com.behsa.sdp.mcwmi.common.DsdpAuthentication;
import com.behsa.sdp.mcwmi.common.RateLimitService;
import com.behsa.sdp.mcwmi.common.ServiceUtils;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String serviceName = ServiceUtils.findServiceNameAndType(request);
        if (serviceName.isEmpty()) {
            logger.error("FORBIDDEN");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
            return false;
        }
        DsdpAuthentication dsdpAuthentication = (DsdpAuthentication) authentication;
        String rateKeyTps = serviceName + "." + dsdpAuthentication.getPrincipal().getUsername() + ".Tps";
        String rateKeyTpd = serviceName + "." + dsdpAuthentication.getPrincipal().getUsername() + ".Tpd";
        PermissionDto permissionDto = dsdpAuthentication.getPermissions().get(serviceName);
        if (permissionDto == null) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }


        Long redisTps = rateLimitService.getTpsTpd(rateKeyTps);
        Long redisTpd = rateLimitService.getTpsTpd(rateKeyTpd);

        if (redisTps == null || redisTps == 0) {
            redisTps = rateLimitService.addTps(rateKeyTps);
        }

        if (redisTpd == null || redisTpd == 0) {
            redisTpd = rateLimitService.addTpd(rateKeyTpd);
        }

//        boolean limitations = rateLimitService.checkAndUpdateLimitation(rateKeyTps, rateKeyTpd, redisTps, redisTpd, permissionDto.getTpd(), permissionDto.getTps());
//        if (!limitations) {
//            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
//            return false;
//        }
        return true;
    }





}