package com.example.orderservice.client;

import com.example.orderservice.config.ProductServiceProperties;
import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.exception.ProductServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ProductServiceClient {

    private final RestClient restClient;
    private final ProductServiceProperties productServiceProperties;

    public ProductServiceClient(RestClient restClient, ProductServiceProperties productServiceProperties) {
        this.restClient = restClient;
        this.productServiceProperties = productServiceProperties;
    }

    public ProductResponse getProduct(Long productId) {
        try {
            return restClient.get()
                    .uri(productServiceProperties.url() + "/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductResponse.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductServiceException("Product not found with ID: " + productId);
            }
            throw new ProductServiceException("Unable to fetch product details");
        }
    }

    public ProductResponse reduceStock(Long productId, int quantity) {
        try {
            return restClient.patch()
                    .uri(productServiceProperties.url() + "/api/products/{id}/stock?quantity={quantity}",
                            productId, quantity)
                    .retrieve()
                    .body(ProductResponse.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ProductServiceException("Insufficient stock for product ID: " + productId);
            }
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductServiceException("Product not found with ID: " + productId);
            }
            throw new ProductServiceException("Unable to update product stock");
        }
    }
}
