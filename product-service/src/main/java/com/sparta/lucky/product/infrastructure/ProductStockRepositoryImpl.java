package com.sparta.lucky.product.infrastructure;

import com.sparta.lucky.product.domain.ProductStock;
import com.sparta.lucky.product.domain.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductStockRepositoryImpl implements ProductStockRepository {

    private final ProductStockRepository productStockRepository;

    @Override
    public ProductStock save(ProductStock stock) {
        return productStockRepository.save(stock);
    }
}
