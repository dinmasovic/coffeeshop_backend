package com.example.coffeeshop.model.disjoint;

import com.example.coffeeshop.model.domain.CoffeeShop;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Drink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected String name;
    protected double price;
    @ManyToOne(fetch = FetchType.EAGER)
    protected CoffeeShop coffeeShop;

    public Drink(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Drink{" +
                "name='" + name + '\'' +
                '}';
    }
}