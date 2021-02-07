package com.behsa.sdp.mcwmi.filters;

import com.behsa.sdp.mcwmi.common.DsdpAuthentication;
import com.behsa.sdp.mcwmi.common.ServiceUtils;
import com.behsa.sdp.mcwmi.dto.Authority;
import com.behsa.sdp.mcwmi.redis.CoreRedis;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthorizationFilter extends OncePerRequestFilter {

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
        return request.getRequestURI().equals("/authenticate")
                || request.getRequestURI().equals("/serviceToken")
                || authentication == null;
    }


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        DsdpAuthentication authentication = (DsdpAuthentication) SecurityContextHolder.getContext().getAuthentication();
         String serviceName = ServiceUtils.findServiceNameAndType(request);

        if (serviceName.isEmpty()) {
            logger.error("FORBIDDEN");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
            chain.doFilter(request, response);
            return;
        }

        if (authentication.getPermissions().containsKey(serviceName)) {
            authentication.addAuthorities(new Authority("SERVICE_ACCESS"));
            authentication.setAuthorized(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(request, response);
    }


}


