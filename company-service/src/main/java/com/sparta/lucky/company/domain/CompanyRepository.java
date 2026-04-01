package com.sparta.lucky.company.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

// Application 레이어가 의존하는 인터페이스 (DIP 적용)
// JPA 구현체는 infrastructure 레이어에 위치
public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findByIdAndDeletedAtIsNull(UUID id);

    Page<Company> searchByName(String name, CompanyType type, UUID hubId, Pageable pageable);
}