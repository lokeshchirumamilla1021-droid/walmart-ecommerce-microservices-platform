package com.example.productservice.controller;

import jakarta.validation.Valid;
import com.example.productservice.entity.Product;
import com.example.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService =productService;

    }

    @PostMapping
    // Create a new Product
    public ResponseEntity<Product> createProduct (@Valid @RequestBody Product product) {
        return new ResponseEntity<>(
                productService.createProduct(product),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    //Get All products
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());

    }

    @GetMapping("/{id}")
    // GET one product by ID using pathvariable
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));

    }

    @PutMapping("/{id}")
    //to update a product
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> reduceStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(productService.reduceStock(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }



















}
