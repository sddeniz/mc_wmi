package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.CacheRestAPI;
import com.behsa.sdp.mc_wmi.config.JwtTokenUtil;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.service.JwtUserDetailsService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RateFilter {//extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CacheRestAPI x;

    @Autowired
    private Gson gson;


   // @Override
    @Order(3)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String requestTokenPermissions = request.getHeader("Authorization");
        if (request.getRequestURI() .equals("/authenticate")) {
            chain.doFilter(request, response);
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse myResponse = (HttpServletResponse) response;
        String pathVariables = request.getRequestURI();
        String serviceName = pathVariables.replace("/api/call/", "");

        String permissions = jwtTokenUtil.getHeadersFromToken(requestTokenPermissions);
        PermissionDto[] permissionDtos = gson.fromJson(permissions, PermissionDto[].class);
        for (PermissionDto permissionDto : permissionDtos) {
            if (permissionDto.getServiceTitle().equals(serviceName)) {
                if (checkLimitation(permissionDto))
                    chain.doFilter(request, response);
                else {
                    responseErrorToUser(httpRequest, response, chain);
                }
            }
        }

        responseErrorToUser(httpRequest, myResponse, chain);


    }



    private void responseErrorToUser(HttpServletRequest httpRequest, HttpServletResponse rateResponse, FilterChain chain) throws IOException, ServletException {
        rateResponse.addHeader("PROFE", "REDIRECTED");
        rateResponse.sendRedirect("redirected");
        rateResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        rateResponse.getOutputStream().flush();
        chain.doFilter(httpRequest, rateResponse);
    }


    private boolean checkLimitation(PermissionDto permissionDto) {

        LimitManager limitManager = new LimitManager(
                Integer.parseInt(permissionDto.getTpd()),
                Integer.parseInt(permissionDto.getMaxBind()),
                Integer.parseInt(permissionDto.getTps()));

        return (limitManager.checkBindLimit() &&
                limitManager.checkDailyLimitTransaction() &&
                limitManager.checkTransactionPerSecond());


        //  new Timer().schedule(new UpdateDayTask(limitManager), 100, 60000);
    }
}
