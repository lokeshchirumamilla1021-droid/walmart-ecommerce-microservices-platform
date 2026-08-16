package com.example.orderservice.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal price,
        Integer stockQuantity
) {
}
