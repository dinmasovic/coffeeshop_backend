package com.example.coffeeshop.service;

import com.example.coffeeshop.model.disjoint.Drink;
import com.example.coffeeshop.model.domain.Order;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrderService {
    public Order findOrderById(Long id);
    public List<Order> getOrders();
    public Order addOrder(Long workerId, Order order);
    public void removeDrink(Long orderId,Long drinkId);
    public Order addDrink(Long orderId,Long drinkId);
    public void deleteOrder(Long workerId,Order order);
}
