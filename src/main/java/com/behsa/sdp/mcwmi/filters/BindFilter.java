package com.behsa.sdp.mcwmi.filters;

import com.behsa.sdp.mcwmi.common.DsdpAuthentication;
import com.behsa.sdp.mcwmi.common.ServiceUtils;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import com.behsa.sdp.mcwmi.redis.CoreRedis;
import com.behsa.sdp.mcwmi.utils.Constants;
import com.behsa.sdp.mcwmi.utils.Utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BindFilter.class);


    private ObjectMapper objectMapper = new ObjectMapper();
    private final CoreRedis coreRedis;

    @Autowired
    private Utils utils;

    public BindFilter(CoreRedis coreRedis) {
        this.coreRedis = coreRedis;
    }

    @PostConstruct
    void init() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return request.getRequestURI().equals("/authenticate")
                || request.getRequestURI().equals("/serviceToken")
                || authentication == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        DsdpAuthentication authentication = (DsdpAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String serviceName = ServiceUtils.findServiceNameAndType(request);
        String requestIp = utils.returnIp(request);// request.getRemoteAddr().trim();
        String rateBindKey = serviceName + "." + authentication.getPrincipal().getUsername() + "." + requestIp;
        PermissionDto permissionDto = authentication.getPermissions().get(serviceName);
        if (permissionDto == null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
            return;
        }
        int ipMaxBind = checkIpAuth(request, permissionDto, requestIp);

        Integer maxBindCount = 250;//permissionDto.getMaxBind().get(requestIp);//todo repair this
        if (ipMaxBind == 0 || maxBindCount == null) {
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

    private int checkIpAuth(HttpServletRequest request, PermissionDto permissionDto, String requestIp) {
        String serviceToken = request.getHeader(Constants.AuthenticationServiceHeader);
        LOGGER.debug("check Ip: serviceToken:{} , MaxBind:{}", serviceToken, permissionDto.getMaxBind());
        if (serviceToken == null && permissionDto.getMaxBind().get("0.0.0.0") == null) { //open add ips
            return permissionDto.getMaxBind().get(requestIp);
        }
        return -1;
    }
}
