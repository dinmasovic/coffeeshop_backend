package com.example.coffeeshop.dto;

import java.util.List;

public record DisplayCoffeeShopDto(String name, List<DisplayWorkerDto> displayWorkerDto) {
}
