package com.jilnash.swecsci361.repo;

import com.jilnash.swecsci361.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
