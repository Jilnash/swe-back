package com.jilnash.swecsci361.repo;

import com.jilnash.swecsci361.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    boolean existsByIdAndQuantityGreaterThan(Long id, Double quantity);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity WHERE p.id = :id")
    void reduceQuantity(@Param("id") Long id, @Param("quantity") Double quantity);
}
