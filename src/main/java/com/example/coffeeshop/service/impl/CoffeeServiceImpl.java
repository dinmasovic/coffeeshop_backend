package com.example.coffeeshop.service.impl;

import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Worker;
import com.example.coffeeshop.repository.CoffeeShopRepository;
import com.example.coffeeshop.service.CoffeeShopService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoffeeServiceImpl implements CoffeeShopService {
    private final CoffeeShopRepository coffeeShopRepository;
    private final PasswordEncoder passwordEncoder;

    public CoffeeServiceImpl(CoffeeShopRepository coffeeShopRepository, PasswordEncoder passwordEncoder) {
        this.coffeeShopRepository = coffeeShopRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Optional<CoffeeShop> getCoffeeShop(Long coffeeShopId) {
        return coffeeShopRepository.findById(coffeeShopId);
    }

    @Override
    public CoffeeShop register(CoffeeShop coffeeShop) {
        if (coffeeShop == null || coffeeShop.getPassword().isEmpty() || coffeeShop.getName().isEmpty()) {
            throw new NullPointerException("coffee shop is null or something is blank");
        }
        coffeeShop.setPassword(passwordEncoder.encode(coffeeShop.getPassword()));
        return coffeeShopRepository.save(coffeeShop);
    }

    @Override
    public CoffeeShop registerWorker(Worker worker) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() instanceof CoffeeShop){
            CoffeeShop tmp = (CoffeeShop) auth.getPrincipal();
            CoffeeShop coffeeShop = coffeeShopRepository.findById(tmp.getId()).get();
            worker.setCoffeeShop(coffeeShop);
            return coffeeShopRepository.save(coffeeShop);
        }
        throw new NullPointerException("Only coffee shop can add workers");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(coffeeShopRepository.findByName(username).isEmpty()){
            throw new UsernameNotFoundException("Username not found");
        }
        return coffeeShopRepository.findByName(username).get();
    }
}
