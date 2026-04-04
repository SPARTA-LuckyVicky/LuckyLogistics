package com.sparta.lucky.notification.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.lucky.notification.common.exception.NotificationErrorCode;
import com.sparta.lucky.notification.common.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class InternalRequestFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH_PREFIX = "/internal/";
    private static final String INTERNAL_HEADER = "X-Internal-Request";
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith(INTERNAL_PATH_PREFIX)) {
            String internalHeader = request.getHeader(INTERNAL_HEADER);
            if (internalHeader == null || internalHeader.isBlank()) {
                sendErrorResponse(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(NotificationErrorCode.ACCESS_DENIED.getHttpStatus());
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> errorResponse = ApiResponse.error(
                NotificationErrorCode.ACCESS_DENIED.getCode(),
                NotificationErrorCode.ACCESS_DENIED.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}