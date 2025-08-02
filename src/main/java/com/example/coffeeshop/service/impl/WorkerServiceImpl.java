package com.example.coffeeshop.service.impl;

import com.example.coffeeshop.model.Roles.Role;
import com.example.coffeeshop.model.domain.CoffeeShop;
import com.example.coffeeshop.model.domain.Order;
import com.example.coffeeshop.model.domain.Worker;
import com.example.coffeeshop.repository.CoffeeShopRepository;
import com.example.coffeeshop.repository.OrderRepository;
import com.example.coffeeshop.repository.WorkerRepository;
import com.example.coffeeshop.service.OrderService;
import com.example.coffeeshop.service.WorkerService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.coffeeshop.model.exceptions.NoSuchWorkerException;

import java.util.List;

@Service
public class WorkerServiceImpl implements WorkerService, UserDetailsService {
    private final WorkerRepository workerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CoffeeShopRepository coffeeShopRepository;


    public WorkerServiceImpl(WorkerRepository workerRepository, PasswordEncoder passwordEncoder, CoffeeShopRepository coffeeShopRepository) {
        this.workerRepository = workerRepository;
        this.passwordEncoder = passwordEncoder;
        this.coffeeShopRepository = coffeeShopRepository;
    }

    @Override
    public Worker registerWorker(Worker worker) {
        if(worker==null || worker.getName().isEmpty() || worker.getPassword().isEmpty() || worker.getRole().name().isEmpty())
            throw new NullPointerException("worker is null or something is blank");
        worker.setPassword(passwordEncoder.encode(worker.getPassword()));
        return workerRepository.save(worker);
    }
    @Override
    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    @Override
    @Transactional
    public Worker getWorker(Long workerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof CoffeeShop){
            CoffeeShop coffeeShop = (CoffeeShop) authentication.getPrincipal();
            coffeeShop = coffeeShopRepository.findById(coffeeShop.getId()).get();
            return coffeeShop.getWorkers().stream().filter(s->s.getId().equals(workerId)).findFirst().orElseThrow(NoSuchWorkerException::new);
        }else{
            Worker worker = (Worker) authentication.getPrincipal();
            worker = workerRepository.findById(workerId).get();
            return worker.getCoffeeShop().getWorkers().stream().filter(s->s.getId().equals(workerId)).findFirst().orElseThrow(NoSuchWorkerException::new);

        }
    }

    @Override
    public List<Worker> getAllWorkers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof CoffeeShop){
            CoffeeShop coffeeShop = (CoffeeShop) authentication.getPrincipal();
            coffeeShop = coffeeShopRepository.findById(coffeeShop.getId()).get();
            return coffeeShop.getWorkers();
        }else{
            Worker worker = (Worker) authentication.getPrincipal();
            worker = workerRepository.findById(worker.getId()).get();
            return worker.getCoffeeShop().getWorkers();

        }
    }

    @Override
    public Worker updateWorker(Long workerId,String name, Role role) {
        Worker worker = workerRepository.findById(workerId).orElseThrow(NoSuchWorkerException::new);
        if(name!=null)
            worker.setName(name);
        if(role!=null)
            worker.setRole(role);
        return workerRepository.save(worker);
    }

    @Override
    public void deleteWorker(Long workerId) {
        workerRepository.deleteById(workerId);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return workerRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("Worker not found: " + username));
    }
}
