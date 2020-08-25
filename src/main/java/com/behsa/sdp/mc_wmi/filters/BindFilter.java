package com.behsa.sdp.mc_wmi.filters;

import com.behsa.sdp.mc_wmi.common.DsdpAuthentication;
import com.behsa.sdp.mc_wmi.dto.MaxBind;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Order(2)
public class BindFilter extends OncePerRequestFilter {

    private ObjectMapper objectMapper = new ObjectMapper();
    private ConcurrentHashMap<String, Long> maxBindMap = new ConcurrentHashMap<>();
    private MaxBind maxBind;


    @PostConstruct
    void init() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        maxBind = new MaxBind();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return request.getRequestURI().equals("/authenticate") || authentication == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        DsdpAuthentication authentication = (DsdpAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserDetails details = (UserDetails) authentication.getPrincipal();
        String serviceName = request.getRequestURI().replace("/api/call/", "");
        String requestIp = request.getRemoteAddr().trim();
        PermissionDto permissionDto = null;
        String rateKey = serviceName + "." + details.getUsername() + "." + requestIp;
        for (GrantedAuthority authority : details.getAuthorities()) {
            permissionDto = (PermissionDto) authority;
            MaxBind[] binds = objectMapper.readValue(permissionDto.getMaxBind(), MaxBind[].class);
            List<MaxBind> maxBindList = Arrays.stream(binds).filter(s -> s.getIp() != null && s.getIp().equals(requestIp)).collect(Collectors.toList());

//            if (maxBindList.isEmpty()) {
//                SecurityContextHolder.getContext().setAuthentication(null);
//                filterChain.doFilter(request, response);
//                return;
//            }

            if (authority.getAuthority().equals(serviceName)) {
                List<MaxBind> collect = maxBindList.stream().filter(s -> s.getIp().equals(requestIp)).collect(Collectors.toList());
                if (collect.isEmpty()) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }

//                else {
//                    if (!validateRateMaxBind(rateKey, collect.get(0).getMaxBind())) {
//
//                        response.sendError(HttpStatus.CREATED.value());//todo change this
//                    }
//                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateRateMaxBind(String key, int maxBind) {
        if (maxBindMap.get(key) == null) {
            maxBindMap.put(key, 1L);
            return true;
        }
        Long maxBindCount = maxBindMap.get(key);
        if (maxBindCount > maxBind)
            return false;

        maxBindMap.put(key, maxBindCount + 1);
        return true;


    }


}
