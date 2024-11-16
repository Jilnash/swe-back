package com.jilnash.swecsci361.repo;

import com.jilnash.swecsci361.model.SoldProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoldProductRepo extends JpaRepository<SoldProduct, Long> {
    List<SoldProduct> findAllByOrderId(Long orderId);
}
