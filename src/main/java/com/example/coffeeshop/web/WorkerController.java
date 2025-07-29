package com.example.coffeeshop.web;

import com.example.coffeeshop.dto.CreateWorkerDto;
import com.example.coffeeshop.dto.DisplayWorkerDto;
import com.example.coffeeshop.model.Roles.Role;
import com.example.coffeeshop.model.domain.Worker;
import com.example.coffeeshop.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/worker")
public class WorkerController {
    private final WorkerService workerService;
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }
    @GetMapping
    public ResponseEntity<List<DisplayWorkerDto>> getWorkers(){
        List<DisplayWorkerDto> displayWorkerDtos= workerService.getAllWorkers().stream().map(s->new DisplayWorkerDto(s.getName())).collect(Collectors.toCollection(ArrayList::new));
        return ResponseEntity.ok(displayWorkerDtos);
    }
    @PostMapping("/add")
    public ResponseEntity<DisplayWorkerDto> addWorker(@RequestBody CreateWorkerDto createWorkerDto) {
        Worker worker = new Worker(createWorkerDto.name(), createWorkerDto.password(), (Role)createWorkerDto.role());
        workerService.registerWorker(worker);
        return ResponseEntity.ok(new DisplayWorkerDto(worker.getName()));
    }
}
