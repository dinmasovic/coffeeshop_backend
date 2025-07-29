package com.example.coffeeshop.dto;

import com.example.coffeeshop.model.Roles.Role;

public record CreateWorkerDto(String name, String password, Role role) {
}
