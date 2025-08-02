package com.example.coffeeshop.service;

import com.example.coffeeshop.model.disjoint.Drink;
import com.example.coffeeshop.model.domain.Order;
import com.example.coffeeshop.model.domain.Worker;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrderService {
    public Order findOrderById(Long id);
    public List<Order> getOrders();
    public Order addOrder(Order order);
    public void removeDrink(Long orderId,Long drinkId);
    public Order addDrink(Long orderId,Long drinkId);
    public void deleteOrder(Order order);
}
