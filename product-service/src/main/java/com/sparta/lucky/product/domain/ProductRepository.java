package com.sparta.lucky.product.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Application 레이어가 의존하는 인터페이스 (DIP 적용)
// JPA 구현체는 infrastructure 레이어에 위치
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    // 목록 조회(검색)
    Page<Product> findAllWithStock(
            String name, ProductStatus status, UUID companyId, UUID hubId, Pageable pageable);

    Optional<Product> findByIdWithStock(UUID productId);

    // 업체 소속 상품 + 재고 일괄 조회 (Soft Delete 미완료 건만 해당)
    List<Product> findAllByCompanyIdWithStock(UUID companyId);



}
