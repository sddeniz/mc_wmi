package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.CacheRestAPI;
import com.behsa.sdp.mc_wmi.config.JwtTokenUtil;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.service.JwtUserDetailsService;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RateFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CacheRestAPI x;

    @Autowired
    private Gson gson;

    @Override
    @Order(3)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final Authentication requestTokenHeader = SecurityContextHolder.getContext().getAuthentication();
        final String requestTokenPermissions = request.getHeader("Authorization");

        String pathVariables = request.getRequestURI();
        String serviceName = pathVariables.replace("/api/call/", "");


        if (requestTokenHeader != null) {
            try {
                String permissions = jwtTokenUtil.getHeadersFromToken(requestTokenPermissions);
                try {
                    PermissionDto[] permissionDtos = gson.fromJson(permissions, PermissionDto[].class);
//                    Arrays.stream(permissionDtos).filter(s->s.getServiceTitle().equals())
                    for (PermissionDto permissionDto : permissionDtos) {
                        if (permissionDto.getServiceTitle().equals(serviceName))
                            chain.doFilter(request, response);

                    }
                } catch (Exception e) {
                    logger.error("token permission have problems");
                }
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not Authorization Header");
        }

    }
}
