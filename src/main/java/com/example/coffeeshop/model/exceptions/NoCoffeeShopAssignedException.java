package com.example.coffeeshop.model.exceptions;

public class NoCoffeeShopAssignedException extends RuntimeException {
    public NoCoffeeShopAssignedException(String message) {
        super(message);
    }
}
