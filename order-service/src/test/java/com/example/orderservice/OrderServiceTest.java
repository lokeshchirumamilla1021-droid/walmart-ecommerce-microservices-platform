package com.example.orderservice;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.exception.ProductServiceException;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderService orderService;

    private ProductResponse product;
    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {
        product = new ProductResponse(
                1L,
                "Walmart Laptop",
                "15-inch laptop",
                "Electronics",
                new BigDecimal("749.99"),
                30
        );

        request = new CreateOrderRequest();
        request.setCustomerEmail("lokesh@example.com");
        request.setProductId(1L);
        request.setQuantity(2);
    }

    @Test
    void shouldCreateOrderWhenStockIsAvailable() {
        when(productServiceClient.getProduct(1L)).thenReturn(product);
        when(productServiceClient.reduceStock(1L, 2)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(10L);
            return order;
        });

        Order savedOrder = orderService.createOrder(request);

        assertEquals(10L, savedOrder.getId());
        assertEquals("Walmart Laptop", savedOrder.getProductName());
        assertEquals(new BigDecimal("1499.98"), savedOrder.getTotalPrice());
        assertEquals(OrderStatus.CONFIRMED, savedOrder.getStatus());
        verify(productServiceClient).reduceStock(1L, 2);
    }

    @Test
    void shouldRejectOrderWhenStockIsInsufficient() {
        ProductResponse lowStockProduct = new ProductResponse(
                1L,
                "Walmart Laptop",
                "15-inch laptop",
                "Electronics",
                new BigDecimal("749.99"),
                1
        );
        when(productServiceClient.getProduct(1L)).thenReturn(lowStockProduct);

        assertThrows(ProductServiceException.class, () -> orderService.createOrder(request));
    }
}
