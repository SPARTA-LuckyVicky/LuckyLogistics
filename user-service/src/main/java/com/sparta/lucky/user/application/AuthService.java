package com.sparta.lucky.user.application;

import com.sparta.lucky.user.application.dto.request.SignupCommand;
import com.sparta.lucky.user.application.dto.response.SignupResult;
import com.sparta.lucky.user.common.exception.BusinessException;
import com.sparta.lucky.user.common.exception.UserErrorCode;
import com.sparta.lucky.user.domain.User;
import com.sparta.lucky.user.domain.UserRepository;
import com.sparta.lucky.user.presentation.dto.request.SignupReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResult signUp(SignupCommand command) {
        if(userRepository.existsByUsername(command.getUsername())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }
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
        attributes.put("role", List.of(command.getRole().name()));
        attributes.put("hubId", List.of(command.getHubId() != null ? command.getHubId() : ""));
        attributes.put("companyId", List.of(command.getCompanyId() != null ? command.getCompanyId() : ""));
        userRepresentation.setAttributes(attributes);

        Response response = keycloak.realm(realm).users().create(userRepresentation);

        if (response.getStatus() == 201) {
            String keycloakId = CreatedResponseUtil.getCreatedId(response);

            User user = command.toEntity(
                    UUID.fromString(keycloakId),
                    passwordEncoder.encode(command.getPassword()));

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

        String encodedPassword = passwordEncoder.encode(reqDto.getPassword());
        SignupCommand signupCommand = SignupCommand.from(reqDto, encodedPassword);

        User user = User.builder()
                .username(signupCommand.getUsername())
                .password(signupCommand.getPassword())
                .name(signupCommand.getName())
                .receiverSlackId(signupCommand.getReceiverSlackId())
                .role(signupCommand.getRole())
                .hubId(signupCommand.getHubId())
                .companyId(signupCommand.getCompanyId())
                .build();
        User savedUser = userRepository.save(user);
        return SignupResult.from(savedUser);
    }

}
