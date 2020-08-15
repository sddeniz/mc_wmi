package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.CacheRestAPI;
import com.behsa.sdp.mc_wmi.config.JwtTokenUtil;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.service.JwtUserDetailsService;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthorizationFilter {//extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CacheRestAPI x;

    @Autowired
    private Gson gson;

    //@Override
    @Order(2)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String requestTokenPermissions = request.getHeader("Authorization");
        if (request.getRequestURI().equals("/authenticate")) {
            chain.doFilter(request, response);
        }

        String pathVariables = request.getRequestURI();
        String serviceName = pathVariables.replace("/api/call/", "");

        if (!serviceName.isEmpty()) {
            try {
                String permissions = jwtTokenUtil.getHeadersFromToken(requestTokenPermissions);
                try {
                    PermissionDto[] permissionDtos = gson.fromJson(permissions, PermissionDto[].class);
                     for (PermissionDto permissionDto : permissionDtos) {
                        if (permissionDto.getServiceTitle().equals(serviceName))
                            chain.doFilter(request, response);
                    }

                } catch (Exception e) {
                  //  logger.error("token permission have problems");
                }
            } catch (IllegalArgumentException e) {
                //logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
             //   logger.error("JWT Token has expired");
            }
        } else {
           // logger.warn("service Name does not Authorization");
        }


    }


}
