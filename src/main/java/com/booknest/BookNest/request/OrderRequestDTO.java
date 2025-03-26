package com.booknest.BookNest.request;

import com.booknest.BookNest.dto.OrderItemDTO;

import java.util.List;

public class OrderRequestDTO {
    private Long userId;
    private List<OrderItemDTO> items;
    private String status;
    private String description;
    private Long shippingAddressId;
    private String paymentMethod;
    private Double totalAmount; 

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getTotalAmount() { 
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) { 
        this.totalAmount = totalAmount;
    }
} 