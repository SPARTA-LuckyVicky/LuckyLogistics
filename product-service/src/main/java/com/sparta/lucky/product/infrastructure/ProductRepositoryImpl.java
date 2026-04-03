package com.sparta.lucky.product.infrastructure;

import com.sparta.lucky.product.domain.Product;
import com.sparta.lucky.product.domain.ProductRepository;
import com.sparta.lucky.product.domain.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Page<Product> findAllWithStock(
            String name, ProductStatus status, UUID companyId, UUID hubId, Pageable pageable) {
        return productJpaRepository.findAllWithStock(name, status, companyId, hubId, pageable);
    }

    @Override
    public Optional<Product> findByIdWithStock(UUID productId) {
        return productJpaRepository.findByIdWithStock(productId);
    }

    @Override
    public List<Product> findAllByCompanyIdWithStock(UUID companyId) {
        return productJpaRepository.findAllByCompanyIdWithStock(companyId);
    }

    @Override
    public Optional<Product> findByIdAndDeletedAtIsNull(UUID id) {
        return productJpaRepository.findByIdAndDeletedAtIsNull(id);
    }


}
