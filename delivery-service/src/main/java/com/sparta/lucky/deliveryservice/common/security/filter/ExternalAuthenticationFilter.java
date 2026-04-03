package com.sparta.lucky.deliveryservice.common.security.filter;

import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.security.auth.ExternalUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ExternalAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/internal");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");

        if(userIdHeader != null && roleHeader != null){
            try {
                UUID userId = UUID.fromString(userIdHeader);
                Role role = Role.valueOf(roleHeader);

                List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

                ExternalUserPrincipal principal =
                    new ExternalUserPrincipal(userId, role);

                Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
                    );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException e) {
                // UUID or Enum 변환 실패
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
