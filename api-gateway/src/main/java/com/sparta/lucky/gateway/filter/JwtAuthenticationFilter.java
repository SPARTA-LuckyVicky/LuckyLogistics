package com.sparta.lucky.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.lucky.gateway.common.exception.AuthErrorCode;
import com.sparta.lucky.gateway.common.response.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret.key}")
    private String secretKey;

    public JwtAuthenticationFilter(){
        super(Config.class);
    }

    public static class Config{
        // 설정 필요할 경우 추가
    }

    // 인증 제외 목록 ( 필요할 경우 추가 )
    private boolean isWhiteList(String path){
        return path.contains("/api/v1/auth/login")||
                path.contains("/api/v1/auth/signup")||
                path.contains("/swagger-ui");
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

    // 토큰 검증 로직 분리, 문제 있을 경우 예외 던짐 (throws)
    private Claims validateAndParseToken(String token) throws ExpiredJwtException, JwtException {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            if(isWhiteList(path)) return chain.filter(exchange);

            String token = extractToken(request);
            if(token == null) return onError(exchange, AuthErrorCode.TOKEN_NOT_FOUND);

            return Mono.just(token)
                    .map(this::validateAndParseToken) // 토큰 파싱 후 Claims 반환
                    .flatMap(claims -> {
                        // 하위 서비스(User, Order 등)가 유저 정보 알 수 있게 헤더 삽입
                        // mutate() 를 사용하여 복사본 생성 후 정보 삽입
                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header("X-User-Id", claims.getSubject()) //유저 ID (UUID 등)
                                .header("X-User-Role",claims.get("role", String.class)) // 유저 권한
                                .build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .onErrorResume(e -> {
                        if (e instanceof ExpiredJwtException) {
                            return onError(exchange, AuthErrorCode.TOKEN_EXPIRED);
                        }
                        if (e instanceof JwtException) {
                            return onError(exchange, AuthErrorCode.INVALID_TOKEN);
                        }
                        return Mono.error(e);
                    });


        });
    }
}
