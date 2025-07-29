package com.example.coffeeshop.model.domain;

import com.example.coffeeshop.model.disjoint.Drink;
import jakarta.persistence.Entity;

@Entity
public class NonAlcoholic extends Drink{
    public NonAlcoholic(String name, double price) {
        super(name, price);
    }
    public NonAlcoholic() {
        super();
    }
}
