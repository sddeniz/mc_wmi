package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.behsa.sdp.mc_wmi.dto.Authority;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        DsdpAuthentication authentication = (DsdpAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String pathVariables = request.getRequestURI();
        String serviceName = pathVariables.replace("/api/call/", "");

        if (serviceName.isEmpty()) {
            logger.error("FORBIDDEN");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN");
            chain.doFilter(request, response);
            return;
        }
        UserDetails details = (UserDetails) authentication.getPrincipal();
        for (GrantedAuthority authority : details.getAuthorities()) {
            if (authority.getAuthority().equals(serviceName)) {
                authentication.addAuthorities(new Authority("SERVICE_ACCESS"));
                authentication.setAuthorized(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
                return;
            }
//            //todo countinue this
//            permissionDto = (PermissionDto) authority;
//            RateLimitInterceptor.maxBind[] binds = objectMapper.readValue(permissionDto.getMaxBind(), RateLimitInterceptor.maxBind[].class);
//            for (RateLimitInterceptor.maxBind bind : binds) {
//                logger.info(request.getRemoteAddr().trim() + " ip user");
//                if (bind.getIp().equals(request.getRemoteAddr().trim())) {
//                    authentication.addAuthorities(new Authority("SERVICE_ACCESS"));
//                    authentication.setAuthorized(true);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    chain.doFilter(request, response);
//                    return;
//                }
//            }

        }
        chain.doFilter(request, response);
    }

}
