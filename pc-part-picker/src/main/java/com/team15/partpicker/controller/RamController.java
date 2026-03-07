package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Ram;
import com.team15.partpicker.model.repository.RamRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rams")
public class RamController {

    private final RamRepository ramRepository;

    public RamController(RamRepository ramRepository) {
        this.ramRepository = ramRepository;
    }

    @GetMapping
    public List<Ram> getRams() {
        return ramRepository.findAll();
    }

    @GetMapping("/{ramId}")
    public Ram getRam(@PathVariable Long ramId) {
        return ramRepository.findById(ramId)
                .orElseThrow(() -> new RuntimeException("Ram not found with id: " + ramId));
    }

    @PostMapping
    public Ram createRam(@Valid @RequestBody Ram ram) {
        return ramRepository.save(ram);
    }
}
