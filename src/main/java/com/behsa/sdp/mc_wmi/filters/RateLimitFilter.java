package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
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
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> bucketMap = new HashMap<>();

    // @Override
    @Order(3)
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
        }
        UserDetails details = (UserDetails) authentication.getPrincipal();

        String rateKey = serviceName + details.getUsername();
        if (!bucketMap.containsKey(rateKey)) {
            PermissionDto permissionDto = null;
            for (GrantedAuthority authority : details.getAuthorities()) {
                if (authority.getAuthority().equals(serviceName)) {
                    permissionDto = (PermissionDto) authority;
                    break;
                }
            }
            if (permissionDto == null) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
                chain.doFilter(request, response);
                return;
            }
            Refill refill = Refill.greedy(permissionDto.getTps(), Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(permissionDto.getTps(), refill).withInitialTokens(1);
            Bucket bucket = Bucket4j.builder().addLimit(limit).build();
            bucketMap.put(rateKey, bucket);
        }
        if (!bucketMap.get(rateKey).tryConsume(1)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
        }
        chain.doFilter(request, response);
    }

}
