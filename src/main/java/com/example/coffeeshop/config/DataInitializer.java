package com.example.coffeeshop.config;

import com.example.coffeeshop.model.Roles.Role;
import com.example.coffeeshop.model.disjoint.Drink;
import com.example.coffeeshop.model.domain.*;
import com.example.coffeeshop.repository.DrinksRepository;
import com.example.coffeeshop.repository.OrderRepository;
import com.example.coffeeshop.repository.WorkerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {
    private final DrinksRepository drinksRepository;
    private final OrderRepository orderRepository;
    private final WorkerRepository workerRepository;

    public DataInitializer(DrinksRepository drinksRepository, OrderRepository orderRepository, WorkerRepository workerRepository) {
        this.drinksRepository = drinksRepository;
        this.orderRepository = orderRepository;
        this.workerRepository = workerRepository;
    }

    @PostConstruct
     void init(){
    }
}
