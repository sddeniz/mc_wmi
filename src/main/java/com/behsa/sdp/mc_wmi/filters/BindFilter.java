package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.redis.CoreRedis;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class BindFilter extends OncePerRequestFilter {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private CoreRedis coreRedis;

    @PostConstruct
    void init() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return request.getRequestURI().equals("/authenticate") || authentication == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        DsdpAuthentication authentication = (DsdpAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String serviceName = request.getRequestURI().replace("/api/call/", "");
        String requestIp = request.getRemoteAddr().trim();
        String rateBindKey = serviceName + "." + authentication.getPrincipal().getUsername() + "." + requestIp;
        PermissionDto permissionDto = authentication.getPermissions().get(serviceName);
        if (permissionDto == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }
        Integer maxBindCount = permissionDto.getMaxBind().get(requestIp);
        if (maxBindCount == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }
        if (!validateRateMaxBind(rateBindKey, maxBindCount)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
        }

        filterChain.doFilter(request, response);
    }
    private boolean validateRateMaxBind(String key, int maxBind) {
        Long maxBindCount = this.coreRedis.getUsage(key);
        if (maxBindCount == null) {
            this.coreRedis.setUsage(key, 1L);
            return true;
        }
        if (maxBindCount > maxBind) {
            return false;
        }
        this.coreRedis.setUsage(key, maxBindCount + 1);
        return true;

    }

}
