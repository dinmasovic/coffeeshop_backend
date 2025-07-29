package com.example.coffeeshop.model.exceptions;

public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException() {
        super("The order does not exist");
    }
}
