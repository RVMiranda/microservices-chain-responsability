package org.rvmiranda.productservice.application.service;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.productservice.domain.model.Product;
import org.rvmiranda.productservice.domain.port.ProductRepositoryPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepositoryPort productRepositoryPort;

    public Product createProduct(Product product) {
        return productRepositoryPort.save(product);
    }

    public Product getProductById(String id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Producto no encontrado con id " + id));
    }

    public List<Product> getAllProducts() {
        return productRepositoryPort.findAll();
    }

    public Product updateProduct(String id, Product updatedProduct) {
        // Primero validamos que exista
        Product existingProduct = getProductById(id);

        // Actualizamos los campos
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());

        return productRepositoryPort.save(existingProduct);
    }

    public void deleteProduct(String id) {
        // Validamos que exista antes de borrar
        getProductById(id);
        productRepositoryPort.deleteById(id);
    }

    public void reduceStock(String id, Integer quantity) {
        Product product = getProductById(id);
        if (product.getStock() < quantity) {
            throw new RuntimeException("Error: Stock insuficiente");
        }
        product.setStock(product.getStock() - quantity);
        productRepositoryPort.save(product);
    }

    public void restoreStock(String id, Integer quantity) {
        Product product = getProductById(id);
        product.setStock(product.getStock() + quantity);
        productRepositoryPort.save(product);
    }
}
