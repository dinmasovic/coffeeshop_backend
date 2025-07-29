package com.example.coffeeshop.repository;

import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoffeeShopRepository extends JpaRepository<CoffeeShop, Long> {
    Optional<CoffeeShop> findByName(String name);

}
