package com.sparta.lucky.user.domain;


import com.sparta.lucky.user.common.entity.BaseEntity;
import com.sparta.lucky.user.common.exception.BusinessException;
import com.sparta.lucky.user.common.exception.UserErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String receiverSlackId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String hubId;
    private String companyId;

    private void validateRole(UserRole role, String hubId, String companyId){
        if(role == UserRole.HUB_MANAGER && hubId == null){
            throw new BusinessException(UserErrorCode.INVALID_HUB_ID);
        }
        if(role == UserRole.COMPANY_MANAGER && companyId == null){
            throw new BusinessException(UserErrorCode.INVALID_COMPANY_ID);
        }
    }

    @Builder
    public User(UUID userId, String username, String password, String name, String receiverSlackId,
                UserRole role, String hubId, String companyId, UserStatus status){
        validateRole(role, hubId, companyId);
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.receiverSlackId = receiverSlackId;
        this.role = role;
        this.hubId = hubId;
        this.companyId = companyId;
        this.status = (status != null) ? status : UserStatus.PENDING;
    }

    public void updateInfo(String name, String slackId, String hubId, String companyId) {
        if (name != null) this.name = name;
        if (slackId != null) this.receiverSlackId = slackId;
        if (hubId != null) this.hubId = hubId;
        if (companyId != null) this.companyId = companyId;
    }

    public void updateStatus(UserStatus newStatus) {

        if (this.status == UserStatus.APPROVED && newStatus == UserStatus.PENDING) {
            throw new BusinessException(UserErrorCode.INVALID_STATUS_CHANGE);
        }
        this.status = newStatus;
    }
}
