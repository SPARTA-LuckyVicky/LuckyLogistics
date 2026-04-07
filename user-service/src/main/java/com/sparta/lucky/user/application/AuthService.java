package com.sparta.lucky.user.application;

import com.sparta.lucky.user.application.dto.request.LoginCommand;
import com.sparta.lucky.user.application.dto.request.SignupCommand;
import com.sparta.lucky.user.application.dto.response.LoginResult;
import com.sparta.lucky.user.application.dto.response.SignupResult;
import com.sparta.lucky.user.common.exception.BusinessException;
import com.sparta.lucky.user.common.exception.UserErrorCode;
import com.sparta.lucky.user.domain.User;
import com.sparta.lucky.user.domain.UserRepository;
import com.sparta.lucky.user.domain.UserRole;
import com.sparta.lucky.user.domain.UserStatus;
import com.sparta.lucky.user.presentation.dto.response.TokenResDto;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.user-client-id}")
    private String clientId;

    @Value("${keycloak.user-client-secret}")
    private String clientSecret;

    // 회원 가입
    @Transactional
    public SignupResult signUp(SignupCommand command) {
        if(userRepository.existsByUsername(command.getUsername())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        //회원가입 첫번째 유저는 권한은 MASTER, 가입상태는 APPROVED 로 주입
        long userCount = userRepository.count();
        UserRole finalRole = (userCount == 0) ? UserRole.MASTER : command.getRole();
        UserStatus finalStatus = (userCount == 0) ? UserStatus.APPROVED : UserStatus.PENDING;

        // keycloak 유저 객체 생성
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(command.getUsername());
        userRepresentation.setFirstName(command.getName());
        userRepresentation.setEnabled(true);

        // 비밀번호 설정
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(command.getPassword());
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(List.of(credentialRepresentation));

        // 커스텀 attributes 설정 (mapper가 읽을 값들)
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("role", List.of(finalRole.name()));
        attributes.put("hubId", List.of(command.getHubId() != null ? command.getHubId() : ""));
        attributes.put("companyId", List.of(command.getCompanyId() != null ? command.getCompanyId() : ""));
        userRepresentation.setAttributes(attributes);

        Response response = keycloak.realm(realm).users().create(userRepresentation);

        if (response.getStatus() == 201) {
            String keycloakId = CreatedResponseUtil.getCreatedId(response);

            User user = command.toEntity(
                    UUID.fromString(keycloakId),
                    passwordEncoder.encode(command.getPassword()),
                    finalRole,
                    finalStatus
            );
            userRepository.save(user);
            return SignupResult.from(user);
        } else {
            throw new BusinessException(UserErrorCode.EXTERNAL_AUTH_ERROR);
        }
    }

    // 로그인
    public LoginResult login(LoginCommand command) {
        try (Keycloak loginKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(command.getUsername())
                .password(command.getPassword())
                .grantType(OAuth2Constants.PASSWORD)
                .build()) {

            AccessTokenResponse response = loginKeycloak.tokenManager().getAccessToken();

            return LoginResult.from(response);

        } catch (Exception e) {
            throw new BusinessException(UserErrorCode.LOGIN_FAILED);
        }
    }

    // 로그아웃
    public void logout(String refreshToken) {
        try {
            // Keycloak의 로그아웃 엔드포인트 호출을 위한 설정
            String logoutUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("refresh_token", refreshToken); // 리프레시 토큰이 있어야 세션 종료 가능

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            restTemplate.postForEntity(logoutUrl, request, String.class);

        } catch (Exception e) {

            throw new BusinessException(UserErrorCode.LOGOUT_FAILED);
        }
    }

    //토큰 재발급
    public TokenResDto refresh(String refreshToken) {
        try {
            String refreshUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "refresh_token");
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            // Keycloak에 재발급 요청
            ResponseEntity<TokenResDto> response = restTemplate.postForEntity(refreshUrl, request, TokenResDto.class);

            return response.getBody();
        } catch (Exception e) {

            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
