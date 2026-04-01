package com.sparta.lucky.company.infrastructure;

import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

// Spring Data JPA — 쿼리 메서드 네이밍 사용
interface CompanyJpaRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByIdAndDeletedAtIsNull(UUID id);

    @Query("SELECT c FROM Company c WHERE " +
            "(:name IS NULL OR c.name LIKE %:name%) AND " +
            "(:type IS NULL OR c.companyType = :type) AND " +
            "(:hubId IS NULL OR c.hubId = :hubId) AND " +
            "c.deletedAt IS NULL")
    Page<Company> findAllByConditions(
            @Param("name") String name,
            @Param("type") CompanyType type,
            @Param("hubId") UUID hubId,
            Pageable pageable
    );
}