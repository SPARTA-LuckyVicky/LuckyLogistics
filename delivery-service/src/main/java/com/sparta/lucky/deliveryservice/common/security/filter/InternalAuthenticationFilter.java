package com.sparta.lucky.deliveryservice.common.security.filter;

import com.sparta.lucky.deliveryservice.common.security.auth.InternalServicePrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class InternalAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().startsWith("/internal");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String isInternalHeader = request.getHeader("X-Internal-Request");

        if(isInternalHeader != null && isInternalHeader.equals("true")) {

            // 추후 내부 API도 서비스 별로 접근 권한을 둔다고 하면 수정해서 사용.
            InternalServicePrincipal principal =
                new InternalServicePrincipal("INTERNAL_SERVICE");

            Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL")) // 내부 시스템이라는 것을 확실히 하기위한 임의의 Role 생성
                );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
