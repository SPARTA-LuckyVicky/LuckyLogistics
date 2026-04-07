package com.sparta.lucky.user.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // 아이디로 조회
    Optional<User> findByUsername(String username);

    // 아이디 중복 확인
    boolean existsByUsername(String username);

    // 전체 유저 페이징 조회 ( 삭제되지 않은 유저만 )
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);

    // 가입 대기 유저 페이징 조회
    Page<User> findAllByStatusAndDeletedAtIsNull(UserStatus status, Pageable pageable);
}
