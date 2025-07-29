package com.example.coffeeshop.repository;

import com.example.coffeeshop.model.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByTableNumber(Long tableNumber);
}
