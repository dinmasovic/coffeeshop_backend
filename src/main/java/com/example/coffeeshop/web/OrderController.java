package com.example.coffeeshop.web;

import com.example.coffeeshop.dto.CreateOrderDto;
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
    public ResponseEntity<List<DisplayOrderDto>> getOrders() {
        //List<DisplayDrinkDto> drinkDtos = orderService.getOrders().stream().flatMap(s->s.getDrinks().stream()).map(s->new DisplayDrinkDto(s.getName(),s.getPrice(),s.getId())).toList();
        List<DisplayOrderDto> orderDto = orderService.getOrders()
                .stream()
                .map(s->new DisplayOrderDto(s.getId(),s.getTableNumber(),s.getDrinks().stream().map(t->new DisplayDrinkDto(t.getName(),t.getPrice(),t.getId())).toList(),new DisplayWorkerDto(s.getWorker().getName()),s.getBillAmount()))
                .toList();
        return ResponseEntity.ok(orderDto);
    }
    @Operation(summary = "Creates a new order and adds it to the corresponding worker", description = "Returns the new order")
    @PostMapping("/add")
    @PreAuthorize("hasRole('WAITER') or hasRole('BARTENDER') or hasRole('MANAGER')")
    public ResponseEntity<DisplayOrderDto> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        try {
            List<Drink> drinks = createOrderDto.drinksId().stream().map(drinksService::getDrinkById).collect(Collectors.toCollection(ArrayList::new));
            Order order=orderService.addOrder(new Order(createOrderDto.tableNumber(),drinks));
            List<DisplayDrinkDto> drinkDtos = order.getDrinks().stream().map(s->new DisplayDrinkDto(s.getName(),s.getPrice(),s.getId())).collect(Collectors.toCollection(ArrayList::new));
            DisplayWorkerDto workerDto = new DisplayWorkerDto(order.getWorker().getName());
            DisplayOrderDto displayOrderDto = new DisplayOrderDto(order.getId(),order.getTableNumber(),drinkDtos,workerDto,order.getBillAmount());
            return ResponseEntity.ok(displayOrderDto);
        } catch (NoDrinkFoundException | NoSuchWorkerException | OrderAlreadyInThatTableException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/add/{orderId}")
    @Operation(summary = "add a drink to an existing order", description = "returns the updated order")
    @PreAuthorize("hasRole('WAITER') or hasRole('BARTENDER') or hasRole('MANAGER')")
    public ResponseEntity<DisplayOrderDto> addDrink(@PathVariable Long orderId,@RequestParam Long drinkId) {
        try{
            Order order = orderService.addDrink(orderId,drinkId);
            List<DisplayDrinkDto> drinkDtos = order.getDrinks().stream().map(s->new DisplayDrinkDto(s.getName(),s.getPrice(),s.getId())).collect(Collectors.toCollection(ArrayList::new));
            DisplayOrderDto dto = new DisplayOrderDto(order.getId(),order.getTableNumber(),drinkDtos,new DisplayWorkerDto(order.getWorker().getName()),order.getBillAmount());
            return ResponseEntity.ok(dto);
        }catch (NoDrinkFoundException | NoSuchWorkerException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/remove/{orderId}")
    @Operation(summary = "remove a drink to an existing order", description = "returns the updated order")
    @PreAuthorize("hasRole('WAITER') or hasRole('BARTENDER') or hasRole('MANAGER')")
    public ResponseEntity<DisplayOrderDto> removeDrink(@PathVariable Long orderId,@RequestParam Long drinkId) {
        try{
            orderService.removeDrink(orderId,drinkId);
            Order order = orderService.findOrderById(orderId);
            List<DisplayDrinkDto> drinkDtos = order.getDrinks().stream().map(s->new DisplayDrinkDto(s.getName(),s.getPrice(),s.getId())).collect(Collectors.toCollection(ArrayList::new));
            DisplayOrderDto dto = new DisplayOrderDto(order.getId(),order.getTableNumber(),drinkDtos,new DisplayWorkerDto(order.getWorker().getName()),order.getBillAmount());
            return ResponseEntity.ok(dto);
        }catch (NoDrinkFoundException | NoSuchWorkerException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/delete/{orderId}")
    @Operation(summary = "remove order from the database",description = "returns info whether it was removed or not")
    @PreAuthorize("hasRole('WAITER') or hasRole('BARTENDER') or hasRole('MANAGER')")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        Order order = orderService.findOrderById(orderId);
        orderService.deleteOrder(order);
        return ResponseEntity.ok("Order has been deleted");
    }
    
}
