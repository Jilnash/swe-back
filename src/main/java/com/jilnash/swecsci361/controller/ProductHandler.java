package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.model.Product;
import com.jilnash.swecsci361.service.ProductService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ProductHandler {

    final private ProductService productService;

    public ProductHandler(ProductService productService) {
        this.productService = productService;
    }

    @MessageMapping("/products/{id}")
    @SendTo("/topic/products/{id}")
    public Product handleProductUpdate(@DestinationVariable Long id) {

        System.out.println("Product update received: " + id);

        return productService.getProduct(id);
    }
}
