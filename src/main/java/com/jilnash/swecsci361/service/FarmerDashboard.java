package com.jilnash.swecsci361.service;

import com.jilnash.swecsci361.model.SoldProduct;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FarmerDashboard {

    private final SoldProductService soldProductService;

    public FarmerDashboard(SoldProductService soldProductService) {
        this.soldProductService = soldProductService;
    }

    public Map<String, Object> getDashboard(String farmerId, Date from, Date to) {

        Map<String, Object> dashboard = new HashMap<>();

        List<SoldProduct> soldProducts = soldProductService.getSoldProducts(farmerId, null, from, to);


        Map<String, List<SoldProduct>> groupedSoldProducts = soldProducts.stream()
                .collect(Collectors.groupingBy(SoldProduct::getName));

        Map<String, Double> productQuantities = groupedSoldProducts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().mapToDouble(SoldProduct::getQuantity).sum()));

        Map<String, Integer> totalSolds = groupedSoldProducts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        // calculate total income
        dashboard.put("income", soldProducts.stream().mapToDouble(sp -> sp.getSoldPrice() * sp.getQuantity()).sum());

        // calculate total quantity of each product
        dashboard.put("productQuantities", productQuantities);

        // calculate total quantity
        dashboard.put("totalQuantity", productQuantities.values().stream().reduce(0.0, Double::sum));

        // calculate total sold quantity of each product
        dashboard.put("totalSolds", totalSolds);

        // calculate total sold quantity
        dashboard.put("totalSold", totalSolds.values().stream().reduce(0, Integer::sum));

        return dashboard;
    }
}
