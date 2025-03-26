package com.booknest.BookNest.service;

import com.booknest.BookNest.model.Book;
import com.booknest.BookNest.request.OrderRequestDTO;
import com.booknest.BookNest.dto.OrderItemDTO;
import com.booknest.BookNest.model.Order;
import com.booknest.BookNest.model.OrderItem;
import com.booknest.BookNest.model.ShippingAddress;
import com.booknest.BookNest.model.User;
import com.booknest.BookNest.repository.BookRepository;
import com.booknest.BookNest.repository.OrderRepository;
import com.booknest.BookNest.repository.ShippingAddressRepository;
import com.booknest.BookNest.repository.UserRepository;
import com.booknest.BookNest.response.OrderResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public Order createOrder(OrderRequestDTO orderRequest) {
        Order order = new Order();
        
        // Set user
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);
        
        // Set shipping address
        ShippingAddress shippingAddress = shippingAddressRepository.findById(orderRequest.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));
        order.setShippingAddress(shippingAddress);
        
        // Set order items
        List<OrderItem> orderItems = orderRequest.getItems().stream().map(itemDTO -> {
            OrderItem orderItem = new OrderItem();
            Book book = bookRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            orderItem.setBook(book);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setOrder(order);
            
            // Decrement stock quantity
            int newStockQuantity = book.getStockQuantity() - itemDTO.getQuantity();
            if (newStockQuantity < 0) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }
            book.setStockQuantity(newStockQuantity);
            bookRepository.save(book); // Save the updated book
            
            return orderItem;
        }).collect(Collectors.toList());
        order.setItems(orderItems);
        
        // Set other fields
        order.setStatus(orderRequest.getStatus());
        order.setDescription(orderRequest.getDescription());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setCreatedAt(LocalDateTime.now());
        
        // Set total amount from user input instead of calculating it
        order.setTotalAmount(orderRequest.getTotalAmount());

        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Gửi email xác nhận đơn hàng
        sendOrderConfirmationEmail(user.getEmail(), savedOrder);
        
        return savedOrder;
    }

    private void sendOrderConfirmationEmail(String email, Order order) {
        String subject = "Xác nhận đơn hàng";
        String content = "<p>Xin chào,</p>"
                + "<p>Đơn hàng của bạn đã được đặt thành công!</p>"
                + "<p>Thông tin đơn hàng:</p>"
                + "<p>ID đơn hàng: " + order.getId() + "</p>"
                + "<p>Tổng số tiền: " + order.getTotalAmount() + " VNĐ</p>"
                + "<p>Trạng thái: Đặt thành công</p>"
                + "<p>Cảm ơn bạn đã mua hàng!</p>";

        try {
            HtmlEmail htmlEmail = new HtmlEmail();
            htmlEmail.setHostName("smtp-relay.brevo.com");
            htmlEmail.setSmtpPort(587);
            htmlEmail.setStartTLSEnabled(true);
            htmlEmail.setAuthentication("h5studiogl@gmail.com", "fScdnZ4WmEDqjBA1");
            htmlEmail.setFrom("h5studiogl@gmail.com");
            htmlEmail.setSubject(subject);
            htmlEmail.setHtmlMsg(content);
            htmlEmail.addTo(email);
            htmlEmail.setCharset("UTF-8");

            htmlEmail.send();
            System.out.println("Email xác nhận đã được gửi đến " + email);
        } catch (EmailException e) {
            System.err.println("Lỗi khi gửi email xác nhận: " + e.getMessage());
        }
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private OrderResponseDTO convertToResponseDTO(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(order.getId());
        responseDTO.setUserId(order.getUser().getId());
        responseDTO.setItems(order.getItems().stream().map(this::convertToOrderItemDTO).collect(Collectors.toList()));
        responseDTO.setTotalAmount(order.getTotalAmount());
        responseDTO.setStatus(order.getStatus());
        responseDTO.setDescription(order.getDescription());
        responseDTO.setShippingAddressId(order.getShippingAddress().getId());
        responseDTO.setPaymentMethod(order.getPaymentMethod());
        responseDTO.setCreatedAt(order.getCreatedAt());
        return responseDTO;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setBookId(orderItem.getBook().getId());
        itemDTO.setQuantity(orderItem.getQuantity());
        return itemDTO;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void cancelOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setStatus("rejected"); 
            orderRepository.save(existingOrder); 
        } else {
            throw new RuntimeException("Order not found");
        }
    }

    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    public List<Book> getBooksByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .flatMap(order -> order.getItems().stream()
                        .map(OrderItem::getBook))
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());
    }
}
