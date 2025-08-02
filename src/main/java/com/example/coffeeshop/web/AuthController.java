package com.example.coffeeshop.web;

import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Worker;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/check")
    public String check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.getPrincipal().equals("anonymousUser")) {
            return "Authentication Success";
        }else
            return "Authentication Failed";
    }
    @GetMapping("/getId")
    public ResponseEntity<Long> getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Worker){
            Worker worker=(Worker)authentication.getPrincipal();
            return ResponseEntity.ok(worker.getId());
        }else{
            CoffeeShop coffeeShop=(CoffeeShop)authentication.getPrincipal();
            return ResponseEntity.ok(coffeeShop.getId());
        }
    }
}
