package com.example.coffeeshop.model.domain;

import com.example.coffeeshop.model.disjoint.Drink;
import jakarta.persistence.Entity;

@Entity
public class Alcoholic extends Drink {
    public Alcoholic(String name, double price) {
        super(name, price);
    }
    public Alcoholic() {
        super();
    }
}
