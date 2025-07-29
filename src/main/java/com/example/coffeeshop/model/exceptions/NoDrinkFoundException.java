package com.example.coffeeshop.model.exceptions;

public class NoDrinkFoundException extends RuntimeException {
    public NoDrinkFoundException() {
        super("The drink does not exist in the menu");
    }
}
