package com.example.coffeeshop.service.impl;

import com.example.coffeeshop.model.disjoint.Drink;
import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Order;
import com.example.coffeeshop.model.domain.Worker;
import com.example.coffeeshop.model.exceptions.*;
import com.example.coffeeshop.repository.CoffeeShopRepository;
import com.example.coffeeshop.repository.DrinksRepository;
import com.example.coffeeshop.repository.OrderRepository;
import com.example.coffeeshop.service.CoffeeShopService;
import com.example.coffeeshop.service.OrderService;
import com.example.coffeeshop.service.WorkerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final DrinksRepository drinksRepository;
    private final WorkerService workerService;
    private final CoffeeShopService coffeeShopService;

    public OrderServiceImpl(OrderRepository orderRepository, DrinksRepository drinksRepository, WorkerService workerService, CoffeeShopService coffeeShopService, CoffeeShopRepository coffeeShopRepository) {
        this.orderRepository = orderRepository;
        this.drinksRepository = drinksRepository;
        this.workerService = workerService;
        this.coffeeShopService = coffeeShopService;
    }
    public void authorizeForAddOrDeleteDrink(Order order,Long drinkId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Worker){
            Worker worker = (Worker) authentication.getPrincipal();
            if(worker.getCoffeeShop()==null){
                throw new NoCoffeeShopAssignedException("The worker has no coffee shop assigned to him");
            }
            if(worker.getOrders().stream().noneMatch(s->s.getTableNumber().equals(order.getTableNumber()))){
                List<Order> orders = worker.getCoffeeShop().getWorkers().stream().flatMap(s->s.getOrders().stream()).collect(Collectors.toCollection(ArrayList::new));
                if(orders.stream().noneMatch(o->o.getTableNumber().equals(order.getTableNumber()))){
                    throw new RuntimeException("The order is not part of your coffee shop");
                }
            }
            List<Long> drinks = worker.getCoffeeShop().getDrinks().stream().map(Drink::getId).collect(Collectors.toCollection(ArrayList::new));
            if(!drinks.contains(drinkId)){
                throw new RuntimeException("The drink is not part of your coffee shop");
            }
        }else{
            CoffeeShop coffeeShop=(CoffeeShop) authentication.getPrincipal();
            coffeeShop=coffeeShopService.getCoffeeShop(coffeeShop.getId()).get();
            List<Order> orders = coffeeShop.getWorkers().stream().flatMap(s->s.getOrders().stream()).collect(Collectors.toCollection(ArrayList::new));
            if(!orders.contains(order)){
                throw new RuntimeException("The order is not part of your coffee shop");
            }
            List<Long> drinks = coffeeShop.getDrinks().stream().map(Drink::getId).collect(Collectors.toCollection(ArrayList::new));
            if(!drinks.contains(drinkId)){
                throw new RuntimeException("The drink is not part of your coffee shop");
            }
        }
    }

    @Override
    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(NoOrderFoundException::new);
    }

    @Override
    public List<Order> getOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Worker){
            Worker worker = (Worker) authentication.getPrincipal();
            worker = workerService.getWorker(worker.getId());
            return worker.getCoffeeShop().getWorkers().stream().flatMap(s->s.getOrders().stream()).collect(Collectors.toCollection(ArrayList::new));
        }else if(authentication.getPrincipal() instanceof CoffeeShop) {
            CoffeeShop coffeeShop = (CoffeeShop) authentication.getPrincipal();
            coffeeShop = coffeeShopService.getCoffeeShop(coffeeShop.getId()).get();
            return coffeeShop.getWorkers().stream().flatMap(s -> s.getOrders().stream()).collect(Collectors.toCollection(ArrayList::new));
        }
        throw new RuntimeException("Unauthorized: Unknown user type or not authenticated");
    }

    @Override
    public Order addOrder(Long workerId, Order order) {
        Worker worker = workerService.getWorker(workerId);
        if(worker.getCoffeeShop()==null){
            throw new NoCoffeeShopAssignedException("The worker has no coffee shop assigned to him");
        }
        if(orderRepository.existsByTableNumber(order.getTableNumber())){
            throw new OrderAlreadyInThatTableException();
        }
        order.setWorker(worker);
        order.setCoffeeShop(worker.getCoffeeShop());
        Order tmp = orderRepository.save(order);
        worker.getOrders().add(tmp);
        workerService.registerWorker(worker);
        return tmp;
    }
    @Override
    public void deleteOrder(Long workerId, Order order) {
        Worker worker = workerService.getWorker(workerId);
        if(worker.getCoffeeShop()==null){
            throw new NoCoffeeShopAssignedException("The worker has no coffee shop assigned to him");
        }
        worker.getOrders().remove(order);
        orderRepository.deleteById(order.getTableNumber());
    }

    @Override
    public void removeDrink(Long orderId,Long drinkId) {
        Drink drink=drinksRepository.findById(drinkId).orElseThrow(NoDrinkFoundException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(NoOrderFoundException::new);
        authorizeForAddOrDeleteDrink(order,drinkId);
        order.getDrinks().remove(drink);
        order.updateBillAmount();
        orderRepository.save(order);
    }


    @Override
    public Order addDrink(Long orderId,Long drinkId) {
        Drink drink=drinksRepository.findById(drinkId).orElseThrow(NoDrinkFoundException::new);
        Order order = orderRepository.findById(orderId).orElseThrow(NoOrderFoundException::new);
        authorizeForAddOrDeleteDrink(order,drinkId);
        order.getDrinks().add(drink);
        order.updateBillAmount();
        return orderRepository.save(order);
    }
}
