package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.dto.OrderDTO;
import com.jilnash.swecsci361.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
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
        orderService.createOrder(orderDto);
        return ResponseEntity.ok("Order created");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody String string) {
        orderService.updateStatus(id, string);
        return ResponseEntity.ok("Order status updated");
    }
}
