package com.sparta.lucky.order.common.filter;

import com.sparta.lucky.order.common.exception.BusinessException;
import com.sparta.lucky.order.common.exception.OrderErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class InternalRequestFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH_PREFIX = "/internal/";
    private static final String INTERNAL_HEADER = "X-Internal-Request";

//    @Value("${security.internal-request.secret}")
//    private String internalRequestSecret;

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
            throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
//        if (!internalRequestSecret.equals(internalRequest)) {
//            throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
//        }
    }
}