package com.example.coffeeshop.service;

import com.example.coffeeshop.model.Roles.Role;
import com.example.coffeeshop.model.domain.Order;
import com.example.coffeeshop.model.domain.Worker;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface WorkerService extends UserDetailsService {
    public Worker registerWorker(Worker worker);
    public Worker getWorker(Long workerId);
    public List<Worker> getAllWorkers();
    public Worker updateWorker(Long workerId,String name, Role role);
    public void deleteWorker(Long workerId);
}
