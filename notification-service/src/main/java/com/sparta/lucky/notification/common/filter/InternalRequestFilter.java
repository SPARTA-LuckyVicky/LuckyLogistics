package com.sparta.lucky.notification.common.filter;

import com.sparta.lucky.notification.common.exception.BusinessException;
import com.sparta.lucky.notification.common.exception.NotificationErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalRequestFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH_PREFIX = "/internal/";
    private static final String INTERNAL_HEADER = "X-Internal-Request";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith(INTERNAL_PATH_PREFIX)) {
            validateInternalRequest(request.getHeader(INTERNAL_HEADER));
        }
        filterChain.doFilter(request, response);
    }

    private void validateInternalRequest(String internalRequest) {
        if (internalRequest == null || internalRequest.isBlank()) {
            throw new BusinessException(NotificationErrorCode.ACCESS_DENIED);
        }
    }
}