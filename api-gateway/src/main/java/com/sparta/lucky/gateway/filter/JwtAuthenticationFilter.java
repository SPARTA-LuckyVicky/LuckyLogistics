package com.sparta.lucky.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.lucky.gateway.common.exception.AuthErrorCode;
import com.sparta.lucky.gateway.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReactiveJwtDecoder jwtDecoder;

    public JwtAuthenticationFilter(ReactiveJwtDecoder jwtDecoder){
        super(Config.class);
        this.jwtDecoder = jwtDecoder;
    }

    public static class Config{
        // 설정 필요할 경우 추가
    }

    // 인증 제외 목록 ( 필요할 경우 추가 )
    private boolean isWhiteList(String path){
        return path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/signup") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    // 토큰 추출 로직 , "Bearer " 뒷부분의 문자열만 추출
    private String extractToken(ServerHttpRequest request){
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    // 에러 발생 시 응답 처리 로직
    // WebFlux 방식(Mono)으로 데이터를 스트림에 담아 보냄
    private Mono<Void> onError(ServerWebExchange exchange, AuthErrorCode errorCode){
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.valueOf(errorCode.getHttpStatus()));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 1. 공통 ApiResponse.error 구조로 데이터 생성
        // 결과: { "success": false, "code": "AUTH_001", "message": "..." }
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());

        // 2. Mono 흐름으로 JSON 변환 및 응답 전송
        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(apiResponse))
                .map(bytes -> response.bufferFactory().wrap(bytes))
                .flatMap(buffer -> {
                    log.error("Gateway Error: [{}] {}", errorCode.getCode(), errorCode.getMessage());
                    return response.writeWith(Mono.just(buffer));
                })
                .onErrorResume(e -> {
                    log.error("JSON 변환 중 예외 발생", e);
                    return response.setComplete();
                });
    }

    private String extractRole(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");

            return roles.stream()
                    .filter(r -> r.equals("MASTER")|| r.equals("HUB_MANAGER") || r.equals("DELIVERY_DRIVER") || r.equals("COMPANY_MANAGER"))
                    .findFirst()
                    .orElse(roles.get(0));
        }

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("account")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> account = (Map<String, Object>) resourceAccess.get("account");
            if (account.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) account.get("roles");
                if (roles != null && !roles.isEmpty()) return roles.get(0);
            }
        }

        return "USER";
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            if(isWhiteList(path)) return chain.filter(exchange);

            String token = extractToken(request);
            if(token == null) return onError(exchange, AuthErrorCode.TOKEN_NOT_FOUND);

            return jwtDecoder.decode(token)
                    .flatMap(jwt -> {
                        String userId = jwt.getSubject();
                        String role = extractRole(jwt);

                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                .header("X-User-Role", role)
                                .build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .onErrorResume(e -> onError(exchange, AuthErrorCode.INVALID_TOKEN));
        });
    }
}
