package com.example.coffeeshop.repository;

import com.example.coffeeshop.model.disjoint.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinksRepository extends JpaRepository<Drink,Long> {
}
