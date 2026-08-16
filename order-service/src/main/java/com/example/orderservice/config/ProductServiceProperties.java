package com.example.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "product.service")
public record ProductServiceProperties(String url) {
}
