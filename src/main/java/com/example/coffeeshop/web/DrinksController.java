package com.example.coffeeshop.web;

import com.example.coffeeshop.dto.CreateDrinkDto;
import com.example.coffeeshop.dto.DisplayDrinkDto;
import com.example.coffeeshop.dto.DisplayWorkerDto;
import com.example.coffeeshop.model.Roles.DrinkType;
import com.example.coffeeshop.model.disjoint.Drink;
import com.example.coffeeshop.model.domain.Alcoholic;
import com.example.coffeeshop.model.domain.Coffee;
import com.example.coffeeshop.model.domain.NonAlcoholic;
import com.example.coffeeshop.model.exceptions.NoCoffeeShopAssignedException;
import com.example.coffeeshop.model.exceptions.NoDrinkFoundException;
import com.example.coffeeshop.service.DrinksService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drinks")
public class DrinksController {
    private final DrinksService drinksService;

    public DrinksController(DrinksService drinksService) {
        this.drinksService = drinksService;
    }
    @GetMapping
    public ResponseEntity<List<DisplayDrinkDto>> getDrinks(){
        try {
            List<DisplayDrinkDto> displayDrinkDtos=drinksService.getDrinks().stream().map(s->new DisplayDrinkDto(s.getName(),s.getPrice())).collect(Collectors.toCollection(ArrayList::new));
            return ResponseEntity.ok(displayDrinkDtos);
        }catch (NoCoffeeShopAssignedException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<DisplayDrinkDto> getDrinkById(@PathVariable long id){
        try {
            DisplayDrinkDto dto = new DisplayDrinkDto(drinksService.getDrinkById(id).getName(),drinksService.getDrinkById(id).getPrice());
            return ResponseEntity.ok(dto);
        }catch (NoDrinkFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/addDrink")
    public ResponseEntity<DisplayDrinkDto> createDrink(@RequestBody CreateDrinkDto drinkDto){
        try {
            if (drinkDto.type().equals(DrinkType.NON_ALCOHOLIC.name())) {
                Drink drink = drinksService.addDrink(new NonAlcoholic(drinkDto.name(), drinkDto.price()));
                return ResponseEntity.ok(new DisplayDrinkDto(drink.getName(),drink.getPrice()));
            } else if (drinkDto.type().equals(DrinkType.ALCOHOLIC.name())) {
                Drink drink = drinksService.addDrink(new Alcoholic(drinkDto.name(), drinkDto.price()));
                return ResponseEntity.ok(new DisplayDrinkDto(drink.getName(),drink.getPrice()));
            } else {
                Drink drink = drinksService.addDrink(new Coffee(drinkDto.name(), drinkDto.price()));
                return ResponseEntity.ok(new DisplayDrinkDto(drink.getName(),drink.getPrice()));
            }
        }catch (NoCoffeeShopAssignedException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/updateDrink/{id}")
    public ResponseEntity<DisplayDrinkDto> updateDrink(@PathVariable Long id,@RequestParam String name,@RequestParam Double price){
        Drink drink = drinksService.updateDrink(id,name,price);
        DisplayDrinkDto dto = new DisplayDrinkDto(drink.getName(),drink.getPrice());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/deleteDrink/{id}")
    public ResponseEntity<String> deleteDrink(@PathVariable Long id){
        drinksService.deleteDrink(id);
        return ResponseEntity.ok("The drink is deleted");
    }
}
