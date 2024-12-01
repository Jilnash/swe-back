package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.model.SoldProduct;
import com.jilnash.swecsci361.service.SoldProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/farms/{farmId}/sold-products")
public class SoldProductController {

    private final SoldProductService soldProductService;

    public SoldProductController(SoldProductService soldProductService) {
        this.soldProductService = soldProductService;
    }

    @GetMapping
    public List<SoldProduct> getSoldProducts(@PathVariable String farmId,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(required = false) Date from,
                                             @RequestParam(required = false) Date to) {

        return soldProductService.getSoldProducts(farmId, status, from, to, null);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status) {
        soldProductService.updateStatus(id, status);
        return ResponseEntity.ok("Status updated succefully");
    }

}
