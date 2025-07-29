package com.example.coffeeshop.web;

import com.example.coffeeshop.dto.DisplayDrinkDto;
import com.example.coffeeshop.dto.DisplayOrderDto;
import com.example.coffeeshop.dto.DisplayWorkerDto;
import com.example.coffeeshop.model.disjoint.Drink;
import com.example.coffeeshop.model.domain.Order;
import com.example.coffeeshop.model.exceptions.NoDrinkFoundException;
import com.example.coffeeshop.model.exceptions.NoSuchWorkerException;
import com.example.coffeeshop.model.exceptions.OrderAlreadyInThatTableException;
import com.example.coffeeshop.service.DrinksService;
import com.example.coffeeshop.service.OrderService;
import com.example.coffeeshop.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final DrinksService drinksService;
    private final WorkerService workerService;
    public OrderController(OrderService orderService, DrinksService drinksService, WorkerService workerService) {
        this.orderService = orderService;
        this.drinksService = drinksService;
        this.workerService = workerService;
    }
    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }
    @Operation(summary = "Creates a new order and adds it to the corresponding worker", description = "Returns the new order")
    @PostMapping("/add/{workerId}")
    @PreAuthorize("hasRole('WAITER') or hasRole('BARTENDER') or hasRole('MANAGER')")
    public ResponseEntity<DisplayOrderDto> createOrder(@PathVariable Long workerId,@RequestParam List<Long> drinksId,@RequestParam Long tableNumber) {
        try {
            List<Drink> drinks = drinksId.stream().map(drinksService::getDrinkById).collect(Collectors.toCollection(ArrayList::new));
            Order order=orderService.addOrder(workerId,new Order(tableNumber,drinks));
            List<DisplayDrinkDto> drinkDtos = order.getDrinks().stream().map(s->new DisplayDrinkDto(s.getName(),s.getPrice())).collect(Collectors.toCollection(ArrayList::new));
            DisplayWorkerDto workerDto = new DisplayWorkerDto(order.getWorker().getName());
            return ResponseEntity.ok(new DisplayOrderDto(order.getTableNumber(),drinkDtos,workerDto,order.getBillAmount()));
        } catch (NoDrinkFoundException | NoSuchWorkerException | OrderAlreadyInThatTableException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/add/{orderId}")
    @Operation(summary = "add a drink to an existing order", description = "returns the updated order")
    public ResponseEntity<DisplayOrderDto> addDrink(@PathVariable Long orderId,@RequestParam Long drinkId) {
        try{
            Order order = orderService.addDrink(orderId,drinkId);
            List<DisplayDrinkDto> drinkDtos = order.getDrinks().stream().map(s->new DisplayDrinkDto(s.getName(),s.getPrice())).collect(Collectors.toCollection(ArrayList::new));
            DisplayOrderDto dto = new DisplayOrderDto(order.getTableNumber(),drinkDtos,new DisplayWorkerDto(order.getWorker().getName()),order.getBillAmount());
            return ResponseEntity.ok(dto);
        }catch (NoDrinkFoundException | NoSuchWorkerException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/remove/{orderId}")
    @Operation(summary = "remove a drink to an existing order", description = "returns the updated order")
    public ResponseEntity<DisplayOrderDto> removeDrink(@PathVariable Long orderId,@RequestParam Long drinkId) {
        try{
            orderService.removeDrink(orderId,drinkId);
            Order order = orderService.findOrderById(orderId);
            List<DisplayDrinkDto> drinkDtos = order.getDrinks().stream().map(s->new DisplayDrinkDto(s.getName(),s.getPrice())).collect(Collectors.toCollection(ArrayList::new));
            DisplayOrderDto dto = new DisplayOrderDto(order.getTableNumber(),drinkDtos,new DisplayWorkerDto(order.getWorker().getName()),order.getBillAmount());
            return ResponseEntity.ok(dto);
        }catch (NoDrinkFoundException | NoSuchWorkerException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
}
