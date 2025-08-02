package com.example.coffeeshop.model.domain;

import com.example.coffeeshop.model.disjoint.Drink;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Data
public class CoffeeShop implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String password;

    @OneToMany(mappedBy = "coffeeShop",fetch = FetchType.EAGER)
    private List<Worker> workers;

    @OneToMany(mappedBy = "coffeeShop",fetch = FetchType.EAGER)
    private List<Drink> drinks;


    public CoffeeShop() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_OWNER"));
    }

    public CoffeeShop(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String toString() {
        return "CoffeeShop{" +
                "name='" + name + '\'' +
                '}';
    }
}
