package com.sparta.lucky.user.application;

import com.sparta.lucky.user.application.dto.request.UserUpdateCommand;
import com.sparta.lucky.user.application.dto.response.UserResult;
import com.sparta.lucky.user.common.exception.BusinessException;
import com.sparta.lucky.user.common.exception.UserErrorCode;
import com.sparta.lucky.user.domain.User;
import com.sparta.lucky.user.domain.UserRepository;
import com.sparta.lucky.user.domain.UserRole;
import com.sparta.lucky.user.domain.UserStatus;
import com.sparta.lucky.user.infrastructure.client.CompanyClient;
import com.sparta.lucky.user.infrastructure.client.dto.AssignManagerReqBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyClient companyClient;

    // 회원 정보 조회, 본인 확인
    @Transactional(readOnly = true)
    public UserResult getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));


        return UserResult.from(user);
    }

    // 전체 유저 페이징 조회 ( 삭제되지 않은 유저만 )
    @Transactional(readOnly = true)
    public Page<UserResult> getAllUsers(Pageable pageable) {
        return userRepository.findAllByDeletedAtIsNull(pageable)
                .map(UserResult::from);
    }

    @Transactional (readOnly = true)
    // 가입 대기 유저 페이징 조회 ( 삭제되지 않은 유저만 )
    public Page<UserResult> getPendingUsers(Pageable pageable) {
        return userRepository.findAllByStatusAndDeletedAtIsNull(UserStatus.PENDING, pageable)
                .map(UserResult::from);
    }

    // 회원 정보 수정
    @Transactional
    public UserResult updateUser(UserUpdateCommand command) {

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BusinessException(UserErrorCode.DELETED_USER);
        }
        user.updateInfo(
                command.getName(),
                command.getReceiverSlackId(),
                command.getUpdateHubId(),
                command.getUpdateCompanyId()
        );
        return UserResult.from(user);
    }

    // 유저 승인/거절
    @Transactional
    public void updateStatus(UUID userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new BusinessException(UserErrorCode.DELETED_USER);
        }
        user.updateStatus(newStatus);
        // 만약 이 유저가 업체 매니저이고, 업체 ID가 있다면?
        if (user.getRole() == UserRole.COMPANY_MANAGER && user.getCompanyId() != null) {
            try {// company-service 호출해서 이 유저를 해당 업체의 매니저로 등록!
            companyClient.assignManager(
                    UUID.fromString(user.getCompanyId()),
                    new AssignManagerReqBody(user.getUserId()),
                    "true" // internalFlag
            );}
            catch (Exception e) {
                log.error("업체 매니저 배정 통신 실패!", e);

            }
        }
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