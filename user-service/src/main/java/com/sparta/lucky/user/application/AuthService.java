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
    public SignupResult signup(SignupReqDto reqDto){


        if(userRepository.existsByUsername(reqDto.getUsername())){
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

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
