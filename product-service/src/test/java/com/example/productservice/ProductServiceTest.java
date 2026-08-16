package com.example.productservice;

import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.exception.ProductNotFoundException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;

import java.util.Optional;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;


    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Walmart Laptop")
                .description("15-inch laptop")
                .category("Electronics")
                .price(new BigDecimal("749.99"))
                .stockQuantity(30)
                .build();


    }


    @Test
    void shouldCreateProduct() {

        //this will arrange fake repository returns for our product
        when(productRepository.save(product)).thenReturn(product);


        Product savedProduct = productService.createProduct(product);
        assertEquals(1L, savedProduct.getId());
        assertEquals("Walmart Laptop", savedProduct.getName());
        assertEquals(new BigDecimal("749.99"), savedProduct.getPrice());

        verify(productRepository).save(product);




    }
    @Test
    void shouldGetProductById() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(1L);

        assertEquals(1L, foundProduct.getId());
        assertEquals("Walmart Laptop", foundProduct.getName());

        verify(productRepository).findById(1L);



    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(99L)
        );

        verify(productRepository).findById(99L);
    }

    @Test
    void shouldGetAllProducts() {
        Product secondProduct = Product.builder()
                .id(2L)
                .name("Walmart Phone")
                .description("Smartphone")
                .category("Electronics")
                .price(new BigDecimal("499.99"))
                .stockQuantity(20)
                .build();
        when(productRepository.findAll())
                .thenReturn(List.of(product, secondProduct));
        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals("Walmart Laptop", products.get(0).getName());
        assertEquals("Walmart Phone", products.get(1).getName());


        verify(productRepository).findAll();



    }



}
