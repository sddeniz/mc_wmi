package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.CacheRestAPI;
import com.behsa.sdp.mc_wmi.config.JwtTokenUtil;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.service.JwtUserDetailsService;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CacheRestAPI x;

    @Autowired
    private Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;

        if (requestTokenHeader != null) {
            try {
                username = jwtTokenUtil.getUsernameFromToken(requestTokenHeader);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token does not Authorization Header");
        }
        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            /**
             *   if token is valid configure Spring Security to manually set authentication
             */
            if (jwtTokenUtil.validateToken(requestTokenHeader, userDetails)) {

                if (methodAut(request,requestTokenHeader)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    /**
                     * After setting the Authentication in the context, we specify
                     *              that the current user is authenticated. So it passes the
                     *             Spring Security Configurations successfully.
                     */
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }

            }
        }

        //  response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        chain.doFilter(request, response);
    }


    private boolean methodAut(HttpServletRequest request, String requestTokenHeader) {

        String pathVariables = request.getRequestURI();
        String serviceName = pathVariables.replace("/api/call/", "");

        if (!serviceName.isEmpty()) {

            String permissions = jwtTokenUtil.getHeadersFromToken(requestTokenHeader);
            PermissionDto[] permissionDtos = gson.fromJson(permissions, PermissionDto[].class);
            for (PermissionDto permissionDto : permissionDtos) {
                if (permissionDto.getServiceTitle().equals(serviceName))
                    return true;

            }
        }
        return false;
    }

}
