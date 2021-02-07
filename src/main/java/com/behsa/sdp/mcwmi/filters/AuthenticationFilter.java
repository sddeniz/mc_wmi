package com.behsa.sdp.mcwmi.filters;

import com.behsa.sdp.mcwmi.common.DsdpAuthentication;
import com.behsa.sdp.mcwmi.redis.CoreRedis;
import com.behsa.sdp.mcwmi.repository.HeaderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    @Autowired
    private CoreRedis coreRedis;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final String authToken = request.getHeader(HeaderKey.AuthenticationHeader);
        final String serviceToken = request.getHeader(HeaderKey.AuthenticationServiceHeader);
        if (authToken == null && serviceToken == null) {
            LOGGER.warn("Token does not Authorization Header");
            chain.doFilter(request, response);
            return;
        }
        DsdpAuthentication authentication;
        if (authToken != null) {
            authentication = this.coreRedis.getAuthentication(authToken);
        } else {
            authentication = this.coreRedis.getAuthenticationServiceToken(serviceToken);
        }
        if (authentication == null) {
            LOGGER.warn("Authentication not found for token {}", authToken);
            chain.doFilter(request, response);
            return;
        }
        /**
         *   if token is valid configure Spring Security to manually set authentication
         */
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);

    }
}
