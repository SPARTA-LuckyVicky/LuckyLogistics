package com.sparta.lucky.user.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GatewayHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 게이트웨이에서 보낸 헤더 추출
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        String hubId = request.getHeader("X-Hub-Id");
        String companyId = request.getHeader("X-Company-Id");

        if (StringUtils.hasText(userId) && StringUtils.hasText(role)) {
            // 1. 권한 설정 (Spring Security 관례에 따라 ROLE_ 접두사 추가)
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // 2. 인증 객체 생성
            // principal에 userId를 넣고, 상세 정보(details)에 hubId와 companyId를 담아둘 수 있습니다.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            // 필요하다면 추가 정보를 담는 Map을 details에 저장
            Map<String, String> details = new HashMap<>();
            details.put("hubId", hubId);
            details.put("companyId", companyId);
            authentication.setDetails(details);

            // 3. SecurityContextHolder에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated User: {}, Role: {}", userId, role);
        }

        filterChain.doFilter(request, response);
    }
}