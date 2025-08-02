package com.example.coffeeshop.service.impl;

import com.example.coffeeshop.model.disjoint.Drink;
import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Worker;
import com.example.coffeeshop.model.exceptions.NoCoffeeShopAssignedException;
import com.example.coffeeshop.model.exceptions.NoDrinkFoundException;
import com.example.coffeeshop.repository.CoffeeShopRepository;
import com.example.coffeeshop.repository.DrinksRepository;
import com.example.coffeeshop.repository.OrderRepository;
import com.example.coffeeshop.repository.WorkerRepository;
import com.example.coffeeshop.service.DrinksService;
import com.example.coffeeshop.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrinksServiceImpl implements DrinksService {
    private final DrinksRepository drinksRepository;
    private final CoffeeShopRepository coffeeShopRepository;
    private final WorkerRepository workerRepository;
    private final OrderRepository orderRepository;

    public DrinksServiceImpl(DrinksRepository drinksRepository, CoffeeShopRepository coffeeShopRepository, WorkerRepository workerRepository, OrderService orderService, OrderRepository orderRepository) {
        this.drinksRepository = drinksRepository;
        this.coffeeShopRepository = coffeeShopRepository;
        this.workerRepository = workerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Drink> getDrinks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Worker){
            Worker tmp = (Worker) authentication.getPrincipal();
            Worker worker = workerRepository.findById(tmp.getId()).get();
            if(worker.getCoffeeShop() != null)
                return worker.getCoffeeShop().getDrinks();
            throw new NoCoffeeShopAssignedException("The worker is not assigned to any coffee shop");
        }else if(authentication.getPrincipal() instanceof CoffeeShop) {
            CoffeeShop coffeeShop = (CoffeeShop) authentication.getPrincipal();
            coffeeShop = coffeeShopRepository.findById(coffeeShop.getId()).get();
            return coffeeShop.getDrinks();
        }
        throw new RuntimeException("Unauthorized: Unknown user type or not authenticated");
    }

    @Override
    public Drink addDrink(Drink drink) {
        if(drink==null)
            throw new NullPointerException("drink is null");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Worker){
            Worker worker = (Worker) authentication.getPrincipal();
            worker = workerRepository.findById(worker.getId()).get();
            if(worker.getCoffeeShop() == null){
                throw new NoCoffeeShopAssignedException("The worker is not assigned to any coffee shop");
            }
            drink.setCoffeeShop(worker.getCoffeeShop());
            Drink savedDrink = drinksRepository.save(drink);
            workerRepository.save(worker);
            return savedDrink;
        }else{
            CoffeeShop shop = (CoffeeShop) authentication.getPrincipal();
            drink.setCoffeeShop(shop);
            Drink savedDrink = drinksRepository.save(drink);
            coffeeShopRepository.save(shop);
            return savedDrink;
        }

    }

    @Override
    public Drink updateDrink(Long drinkID, String name,Double price) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Drink tmp;//drinksRepository.findById(drinkID).orElseThrow(NoDrinkFoundException::new);
        if(authentication.getPrincipal() instanceof CoffeeShop){
            CoffeeShop shop = (CoffeeShop) authentication.getPrincipal();
            CoffeeShop coffeeShop = coffeeShopRepository.findById(shop.getId()).get();
            tmp =coffeeShop.getDrinks().stream().filter(s->s.getId().equals(drinkID)).findFirst().orElseThrow(NoDrinkFoundException::new);
        }else{
            Worker worker = (Worker) authentication.getPrincipal();
            worker=workerRepository.findById(worker.getId()).get();
            if(worker.getCoffeeShop() == null){
                throw new NoCoffeeShopAssignedException("The worker is not assigned to any coffee shop");
            }
            tmp =worker.getCoffeeShop().getDrinks().stream().filter(s->s.getId().equals(drinkID)).findFirst().orElseThrow(NoDrinkFoundException::new);
        }
        if(name!=null)
        tmp.setName(name);
        if(price!=null)
        tmp.setPrice(price);
        return drinksRepository.save(tmp);
    }

    @Override
    public void deleteDrink(Long drinkID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof CoffeeShop) {
            CoffeeShop shop = (CoffeeShop) authentication.getPrincipal();
            if(shop.getDrinks().stream().anyMatch(s->s.getId().equals(drinkID))){
                shop.getWorkers()
                        .stream().flatMap(s->s.getOrders().stream())
                            .filter(s->s.getDrinks().stream().anyMatch(d->d.getId().equals(drinkID)))
                            .forEach(s->{s.getDrinks().removeIf(t->t.getId().equals(drinkID));
                                    s.updateBillAmount();
                                    orderRepository.save(s);
                            });
            }
            drinksRepository.deleteById(drinkID);
        }else{
            Worker worker = (Worker) authentication.getPrincipal();
            worker=workerRepository.findById(worker.getId()).get();
            if(worker.getCoffeeShop() == null){
                throw new NoCoffeeShopAssignedException("The worker is not assigned to any coffee shop");
            }
            if(worker.getCoffeeShop().getDrinks().stream().anyMatch(s->s.getId().equals(drinkID))){
                worker.getCoffeeShop().getWorkers()
                        .stream().flatMap(s->s.getOrders().stream())
                        .filter(s->s.getDrinks().stream().anyMatch(d->d.getId().equals(drinkID)))
                        .forEach(s->{s.getDrinks().removeIf(t->t.getId().equals(drinkID));
                            s.updateBillAmount();
                            orderRepository.save(s);
                        });
            }
            drinksRepository.deleteById(drinkID);
        }
    }

    @Override
    public Drink getDrinkById(Long drinkID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof CoffeeShop){
            CoffeeShop tmp = (CoffeeShop) authentication.getPrincipal();
            CoffeeShop coffeeShop = coffeeShopRepository.findById(tmp.getId()).get();
            return coffeeShop.getDrinks().stream().filter(s->s.getId().equals(drinkID)).findFirst().orElseThrow(NoDrinkFoundException::new);
        }else{
            Worker tmp = (Worker) authentication.getPrincipal();
            tmp=workerRepository.findById(tmp.getId()).get();
            return tmp.getCoffeeShop().getDrinks().stream().filter(s->s.getId().equals(drinkID)).findFirst().orElseThrow(NoDrinkFoundException::new);
        }
    }
}
