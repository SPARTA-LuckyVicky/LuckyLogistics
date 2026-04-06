package com.sparta.lucky.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // 아이디로 조회
    Optional<User> findByUsername(String username);

    // 특정 status의 사용자 목록 조회 ( 가입 신청 목록 조회용 등 )
    List<User> findAllByStatus(UserStatus status);

    // 아이디 중복 확인
    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
}
