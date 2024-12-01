package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.model.SoldProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerDashboard {

    private final SoldProductService soldProductService;

    public Map<String, Object> getDashboard(String buyerId, Date from, Date to) {

        Map<String, Object> dashboard = new HashMap<>();

        List<SoldProduct> soldProducts = soldProductService.getSoldProducts(null, null, from, to, buyerId);


        Map<String, List<SoldProduct>> groupedSoldProducts = soldProducts.stream()
                .collect(Collectors.groupingBy(SoldProduct::getName));

        Map<String, Double> productQuantities = groupedSoldProducts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().mapToDouble(SoldProduct::getQuantity).sum()));

        Map<String, Integer> totalBoughts = groupedSoldProducts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        // calculate total income
        dashboard.put("expenditure", soldProducts.stream().mapToDouble(sp -> sp.getSoldPrice() * sp.getQuantity()).sum());

        // calculate total quantity of each product
        dashboard.put("productQuantities", productQuantities);

        // calculate total quantity
        dashboard.put("totalQuantity", productQuantities.values().stream().reduce(0.0, Double::sum));

        // calculate total sold quantity of each product
        dashboard.put("totalBoughts", totalBoughts);

        // calculate total sold quantity
        dashboard.put("totalBoughts", totalBoughts.values().stream().reduce(0, Integer::sum));

        return dashboard;
    }
}
