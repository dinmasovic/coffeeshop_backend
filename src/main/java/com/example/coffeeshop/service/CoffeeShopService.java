package com.example.coffeeshop.service;

import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Worker;
import jakarta.security.auth.message.config.AuthConfigProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface CoffeeShopService extends UserDetailsService {
     Optional<CoffeeShop> getCoffeeShop(Long coffeeShopId);
     CoffeeShop register(CoffeeShop coffeeShop);
     CoffeeShop registerWorker(Worker worker);
     void removeWorker(Worker worker);
}
