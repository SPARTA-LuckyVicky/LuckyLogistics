package com.sparta.lucky.hub.common.filter;

import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class InternalRequestFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH_PREFIX = "/internal/";
    private static final String INTERNAL_HEADER = "X-Internal-Request";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith(INTERNAL_PATH_PREFIX)) {
            validateInternalRequest(request.getHeader(INTERNAL_HEADER));
        }

        filterChain.doFilter(request, response);
    }

    private void validateInternalRequest(String internalRequest) {
        if (internalRequest == null || internalRequest.isBlank()) {
            throw new BusinessException(HubErrorCode.HUB_ACCESS_DENIED);
        }
    }
}