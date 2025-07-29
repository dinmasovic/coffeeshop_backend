package com.example.coffeeshop.model.exceptions;

public class OrderAlreadyInThatTableException extends RuntimeException {
    public OrderAlreadyInThatTableException() {
        super("There is already an order on that table");
    }
}
