package com.example.productservice.service;

import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.example.productservice.exception.InsufficientStockException;
import com.example.productservice.exception.ProductNotFoundException;

import java.util.List;

@Service

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow (()  -> new ProductNotFoundException(id));



    }
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());

        return productRepository.save(product);

    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public Product reduceStock(Long id, int quantity) {
        Product product = getProductById(id);

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    id, quantity, product.getStockQuantity()
            );
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }
}
