package com.example.coffeeshop.model.exceptions;

public class NoSuchWorkerException extends RuntimeException {
    public NoSuchWorkerException() {
        super("The worker does not exist");
    }
}
