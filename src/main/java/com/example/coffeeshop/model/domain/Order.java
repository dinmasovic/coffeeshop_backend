package com.example.coffeeshop.model.domain;

import com.example.coffeeshop.model.disjoint.Drink;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    private Long tableNumber;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Drink> drinks;
    @ManyToOne(fetch = FetchType.EAGER)
    private Worker worker;
    @ManyToOne(fetch = FetchType.EAGER)
    private CoffeeShop coffeeShop;
    private double billAmount;
    public Order(Long tableNumber,List<Drink> drinks) {
        this.tableNumber=tableNumber;
        this.drinks = drinks;
        billAmount = drinks.stream().mapToDouble(Drink::getPrice).sum();
    }
    public Order() {
        drinks = new ArrayList<>();
    }
    public void updateBillAmount() {
        billAmount = drinks.stream().mapToDouble(Drink::getPrice).sum();
    }
}
