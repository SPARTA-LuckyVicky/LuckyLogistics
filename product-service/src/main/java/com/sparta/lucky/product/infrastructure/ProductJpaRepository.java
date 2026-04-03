package com.sparta.lucky.product.infrastructure;

import com.sparta.lucky.product.domain.Product;
import com.sparta.lucky.product.domain.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ProductJpaRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    // product 목록 페이지 조회
    // JOIN FETCH + Pageable 조합은 Hibernate가 전체 결과를 메모리에 로드 후 페이지 처리 (HHH000104 경고)
    // → @EntityGraph로 변경: DB 레벨 페이지네이션 + stock EAGER 로딩 분리
    // stock의 deletedAt 조건은 product와 생명주기가 동일하므로 product.deletedAt IS NULL 만으로 충분
    @EntityGraph(attributePaths = {"stock"})
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:companyId IS NULL OR p.companyId = :companyId) AND " +
            "(:hubId IS NULL OR p.hubId = :hubId) AND " +
            "p.deletedAt IS NULL")
    Page<Product> findAllWithStock(
            @Param("name") String name,
            @Param("status") ProductStatus status,
            @Param("companyId") UUID companyId,
            @Param("hubId") UUID hubId,
            Pageable pageable
    );

    // id로 단건조회 + stock JOIN FETCH + soft delete 필터
    @Query("SELECT p FROM Product p JOIN FETCH p.stock ps WHERE " +
            "p.id = :productId AND p.deletedAt IS NULL AND ps.deletedAt IS NULL")
    Optional<Product> findByIdWithStock(@Param("productId") UUID productId);

    // 업체 소속 상품 + 재고 JOIN FETCH - 업체 삭제시 상품 일괄 Soft Delete용
    @Query("SELECT p FROM Product p JOIN FETCH p.stock ps " +
            "WHERE p.companyId = :companyId AND p.deletedAt IS NULL AND ps.deletedAt IS NULL")
    List<Product> findAllByCompanyIdWithStock(@Param("companyId") UUID companyId);


}
