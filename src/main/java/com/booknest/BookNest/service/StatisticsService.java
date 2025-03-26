package com.booknest.BookNest.service;

import com.booknest.BookNest.model.Order;
import com.booknest.BookNest.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    @Autowired
    private OrderRepository orderRepository;

    public Map<LocalDate, Double> getRevenueByDay() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().toLocalDate(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));
    }

    public Map<String, Double> getRevenueByMonth() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().getMonth().toString() + " " + order.getCreatedAt().getYear(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));
    }
}
