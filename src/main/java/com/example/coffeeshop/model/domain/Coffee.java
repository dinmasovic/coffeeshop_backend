package com.example.coffeeshop.model.domain;

import com.example.coffeeshop.model.disjoint.Drink;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.repository.cdi.Eager;

@Entity
public class Coffee extends Drink {
    public Coffee(String name, double price) {
        super(name, price);
    }
    public Coffee() {
        super();
    }
}
