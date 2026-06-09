package org.rvmiranda.productservice.application.service;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.productservice.domain.model.Product;
import org.rvmiranda.productservice.domain.port.ProductRepositoryPort;
import org.rvmiranda.productservice.infraestructure.client.OrderClient;
import feign.FeignException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepositoryPort productRepositoryPort;
    private final OrderClient orderClient;

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

        // Validar si el producto está asociado a alguna orden en ordenservice
        try {
            var response = orderClient.existsOrderByProductId(id);
            if (response != null && response.getData() != null && response.getData()) {
                throw new RuntimeException("Error: No se puede eliminar el producto porque está asociado a una o más órdenes.");
            }
        } catch (FeignException e) {
            throw new RuntimeException("Error al verificar asociaciones del producto con órdenes: " + e.getMessage());
        }

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
