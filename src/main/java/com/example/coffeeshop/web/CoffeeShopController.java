package com.example.coffeeshop.web;

import com.example.coffeeshop.dto.CreateCoffeeShopDto;
import com.example.coffeeshop.dto.DisplayCoffeeShopDto;
import com.example.coffeeshop.dto.DisplayWorkerDto;
import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Worker;
import com.example.coffeeshop.model.exceptions.NoSuchWorkerException;
import com.example.coffeeshop.repository.WorkerRepository;
import com.example.coffeeshop.service.CoffeeShopService;
import com.example.coffeeshop.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coffeeshop")
public class CoffeeShopController{
    private final CoffeeShopService coffeeShopService;
    private final WorkerService workerService;
    private final WorkerRepository workerRepository;

    public CoffeeShopController(CoffeeShopService coffeeShopService, WorkerService workerService, WorkerRepository workerRepository) {
        this.coffeeShopService = coffeeShopService;
        this.workerService = workerService;
        this.workerRepository = workerRepository;
    }
    @GetMapping
    public ResponseEntity<DisplayCoffeeShopDto> getCoffeeShop(@RequestParam Long id) {
        if(coffeeShopService.getCoffeeShop(id).isPresent()){
            List<DisplayWorkerDto> workerDtos = new ArrayList<>();
            if(coffeeShopService.getCoffeeShop(id).get().getWorkers() != null)
                workerDtos = coffeeShopService.getCoffeeShop(id).get().getWorkers().stream().map(s->new DisplayWorkerDto(s.getName())).collect(Collectors.toCollection(ArrayList::new));
            return ResponseEntity.ok(new DisplayCoffeeShopDto(coffeeShopService.getCoffeeShop(id).get().getName(),workerDtos));
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/register")
    public ResponseEntity<DisplayCoffeeShopDto> registerCoffeeShop(@RequestBody CreateCoffeeShopDto coffeeShopDto) {
        try{
            CoffeeShop coffeeShop = coffeeShopService.register(new CoffeeShop(coffeeShopDto.name(),coffeeShopDto.password()));
            List<DisplayWorkerDto> workerDtos = new ArrayList<>();
            return ResponseEntity.ok(new DisplayCoffeeShopDto(coffeeShop.getName(),workerDtos));
        }catch (NullPointerException e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/addworker")
    public ResponseEntity<String> addWorker(@RequestParam Long workerId){
        try{
            Worker worker = workerRepository.findById(workerId).orElseThrow(NoSuchWorkerException::new);
            coffeeShopService.registerWorker(worker);
            return ResponseEntity.ok("The worker is added to the coffee shop");
        }catch (NullPointerException | NoSuchWorkerException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
