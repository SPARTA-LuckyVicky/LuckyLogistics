package com.sparta.lucky.user.application;

import com.sparta.lucky.user.application.dto.request.UserUpdateCommand;
import com.sparta.lucky.user.application.dto.response.UserResult;
import com.sparta.lucky.user.common.exception.BusinessException;
import com.sparta.lucky.user.common.exception.ErrorCode;
import com.sparta.lucky.user.common.exception.UserErrorCode;
import com.sparta.lucky.user.domain.User;
import com.sparta.lucky.user.domain.UserRepository;
import com.sparta.lucky.user.domain.UserStatus;
import com.sparta.lucky.user.presentation.dto.response.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 정보 조회, 본인 확인
    @Transactional(readOnly = true)
    public UserResult getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));


        return UserResult.from(user);
    }

    // 전체 유저 페이징 조회 ( 삭제되지 않은 유저만 )
    @Transactional(readOnly = true)
    public Page<UserResDto> getAllUsers(Pageable pageable) {
        return userRepository.findAllByDeletedAtIsNull(pageable)
                .map(UserResult::from)
                .map(UserResDto::from);
    }

    // 가입 대기 유저 페이징 조회 ( 삭제되지 않은 유저만 )
    public Page<UserResDto> getPendingUsers(Pageable pageable) {
        Page<User> pendingUserPage = userRepository.findAllByStatusAndDeletedAtIsNull(UserStatus.PENDING, pageable);
        return pendingUserPage
                .map(UserResult::from)
                .map(UserResDto::from);
    }

    // 회원 정보 수정
    @Transactional
    public UserResult updateUser(UserUpdateCommand command) {

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateInfo(
                command.getName(),
                command.getReceiverSlackId(),
                command.getHubId(),
                command.getCompanyId()
        );

        return UserResult.from(user);
    }

    // 유저 승인/거절
    @Transactional
    public void updateStatus(UUID userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateStatus(newStatus);
    }

    // 유저 삭제
    @Transactional
    public void deleteUser(UUID userId, UUID loginUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BusinessException(UserErrorCode.DELETED_USER);
        }

        user.softDelete(loginUserId);


    }
}