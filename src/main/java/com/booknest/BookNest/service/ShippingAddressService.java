package com.booknest.BookNest.service;

import com.booknest.BookNest.exception.ShippingAddressException;
import com.booknest.BookNest.repository.OrderRepository;
import com.booknest.BookNest.model.Order;
import com.booknest.BookNest.model.ShippingAddress;
import com.booknest.BookNest.repository.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingAddressService {
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private OrderRepository orderRepository;

    public ShippingAddress addShippingAddress(ShippingAddress address) {
        return shippingAddressRepository.save(address);
    }

    public List<ShippingAddress> getShippingAddressesByUserId(Long userId) {
        return shippingAddressRepository.findByUserId(userId);
    }

    public void deleteShippingAddress(Long id) {
        List<Order> orders = orderRepository.findByShippingAddressId(id);
        if (!orders.isEmpty()) {
            throw new ShippingAddressException("Cannot delete shipping address as it is referenced by existing orders.");
        }
        shippingAddressRepository.deleteById(id);
    }

    public List<ShippingAddress> getAllShippingAddresses() {
        return shippingAddressRepository.findAll();
    }
}
