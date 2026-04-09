package org.rvmiranda.productservice.infraestructure.repository;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.productservice.domain.model.Product;
import org.rvmiranda.productservice.domain.port.ProductRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final SpringDataMongoProductRepository mongoRepository;

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity savedEntity = mongoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    private ProductEntity toEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    private Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }
}
