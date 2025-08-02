package com.example.coffeeshop.dto;

import java.util.List;

public record DisplayOrderDto(Long orderId,Long table_num, List<DisplayDrinkDto> drinks, DisplayWorkerDto worker, double bill) {
}
