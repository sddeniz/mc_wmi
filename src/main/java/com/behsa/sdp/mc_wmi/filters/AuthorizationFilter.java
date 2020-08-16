package com.behsa.sdp.mc_wmi.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (request.getRequestURI().equals("/authenticate") || authentication == null) {
            chain.doFilter(request, response);
            return;
        }

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
                chain.doFilter(request, response);
                return;
            }
        }
        SecurityContextHolder.getContext().setAuthentication(null);//todo moj
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        chain.doFilter(request, response);
    }


}
