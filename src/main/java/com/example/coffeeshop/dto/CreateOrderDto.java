package com.example.coffeeshop.dto;


import java.util.List;

public record CreateOrderDto(List<Long> drinksId, Long tableNumber) {
}
