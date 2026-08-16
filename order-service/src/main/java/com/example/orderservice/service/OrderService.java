package com.example.orderservice.service;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.exception.ProductServiceException;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    public OrderService(OrderRepository orderRepository, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        ProductResponse product = productServiceClient.getProduct(request.getProductId());

        if (product.stockQuantity() < request.getQuantity()) {
            throw new ProductServiceException(
                    "Insufficient stock for product '" + product.name() + "'. Available: " + product.stockQuantity()
            );
        }

        productServiceClient.reduceStock(request.getProductId(), request.getQuantity());

        BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .customerEmail(request.getCustomerEmail())
                .productId(product.id())
                .productName(product.name())
                .quantity(request.getQuantity())
                .unitPrice(product.price())
                .totalPrice(totalPrice)
                .status(OrderStatus.CONFIRMED)
                .build();

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getOrdersByCustomerEmail(String customerEmail) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
}
