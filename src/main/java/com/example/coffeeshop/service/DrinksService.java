package com.example.coffeeshop.service;

import com.example.coffeeshop.model.disjoint.Drink;

import java.util.List;

public interface DrinksService {
    public List<Drink> getDrinks();
    public Drink addDrink(Drink drink);
    public Drink updateDrink(Long drinkID,String name,Double price);
    public void deleteDrink(Long drinkID);
    public Drink getDrinkById(Long drinkID);
}
