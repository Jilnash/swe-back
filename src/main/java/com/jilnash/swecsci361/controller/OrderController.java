package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.dto.OrderDTO;
import com.jilnash.swecsci361.dto.User;
import com.jilnash.swecsci361.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    private final HttpServletRequest request;

    public OrderController(OrderService orderService, HttpServletRequest request) {
        this.orderService = orderService;
        this.request = request;
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam(required = false) String buyerId,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) Date createdAfter,
                                       @RequestParam(required = false) Date createdBefore) {
        return ResponseEntity.ok(
                orderService.getOrders(buyerId, status, createdAfter, createdBefore)
        );
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + request.getHeader("Authorization").substring(7));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        orderDto.setBuyerId(
                new RestTemplate().exchange(
                        "http://92.46.74.132:8888/user/me", HttpMethod.GET, entity, User.class
                ).getBody().getUuid()
        );

        orderService.createOrder(orderDto);
        return ResponseEntity.ok("Order created");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody String string) {
        orderService.updateStatus(id, string);
        return ResponseEntity.ok("Order status updated");
    }
}
