package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.dto.Filter;
import com.jilnash.swecsci361.dto.ProductCreateDTO;
import com.jilnash.swecsci361.dto.ProductUpdateDTO;
import com.jilnash.swecsci361.service.ProductService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ProductController(ProductService productService, SimpMessagingTemplate simpMessagingTemplate) {
        this.productService = productService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> getProducts(@Validated
                                         @RequestParam(defaultValue = "updatedAt")
                                         @Pattern(regexp = "updatedAt|price|popularity") String sort,
                                         @RequestParam(defaultValue = "desc") @Pattern(regexp = "asc|desc") String order,
                                         @Validated @RequestBody(required = false) Filter filter) {

        return ResponseEntity.ok(productService.getProducts(sort, order, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDTO(id));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<?> getProductImages(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductImages(id));
    }

    @PostMapping(consumes = {"application/octet-stream", "multipart/form-data"})
    public ResponseEntity<?> createProduct(
            @RequestPart("images") List<MultipartFile> images,
            @Validated @RequestPart("productDTO") ProductCreateDTO productDTO) {

        return ResponseEntity.ok(productService.createProduct(images, productDTO));
    }

    @PutMapping("/{id}/data")
    public ResponseEntity<?> updateProduct(@Validated @RequestBody ProductUpdateDTO productDTO) {

        simpMessagingTemplate.convertAndSend(
                "/topic/products/" + productDTO.getId(),
                productService.updateProduct(productDTO)
        );

        return ResponseEntity.ok("Product updated successfully");
    }

    @PutMapping("/{id}/images")
    public ResponseEntity<?> updateProductImages(@PathVariable Long id,
                                                 @RequestPart("images") List<MultipartFile> images) {
        productService.updateProductImages(id, images);

        simpMessagingTemplate.convertAndSend(
                "/topic/products/" + id,
                "images"
        );

        return ResponseEntity.ok("Images updated successfully");
    }
}
