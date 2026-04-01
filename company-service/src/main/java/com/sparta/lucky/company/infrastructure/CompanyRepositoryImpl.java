package com.sparta.lucky.company.infrastructure;

import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyRepository;
import com.sparta.lucky.company.domain.CompanyType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// Domain 인터페이스 구현체 — JpaRepository를 감싸서 Application에 노출
@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {

    private final CompanyJpaRepository jpaRepository;

    @Override
    public Company save(Company company) {
        return jpaRepository.save(company);
    }

    @Override
    public Optional<Company> findByIdAndDeletedAtIsNull(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Page<Company> searchByName(String name, CompanyType type, UUID hubId, Pageable pageable) {
        return jpaRepository.findAllByConditions(name, type, hubId, pageable);
    }


}