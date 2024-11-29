package com.jilnash.swecsci361.controller;

import com.jilnash.swecsci361.service.FarmerDashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashBoardController {

    private final FarmerDashboard farmerDashboard;

//    private final BuyerDashboard buyerDashboard;

    @GetMapping("/farmer")
    public ResponseEntity<?> getFarmerDashboard(@RequestParam String farmerId,
                                                @RequestParam(required = false) Date from,
                                                @RequestParam(required = false) Date to) {

        return ResponseEntity.ok(farmerDashboard.getDashboard(farmerId, from, to));
    }

//    @GetMapping("/buyer")
//    public ResponseEntity<?> getBuyerDashboard(@RequestParam String buyerId,
//                                               @RequestParam(required = false) Date from,
//                                               @RequestParam(required = false) Date to) {
//
//        return ResponseEntity.ok(buyerDashboard.getDashboard(buyerId, from, to));
//    }
}
