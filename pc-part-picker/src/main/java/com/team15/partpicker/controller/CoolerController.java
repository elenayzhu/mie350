package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Cooler;
import com.team15.partpicker.model.repository.CoolerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/coolers")
public class CoolerController {

    private final CoolerRepository coolerRepository;

    public CoolerController(CoolerRepository coolerRepository) {
        this.coolerRepository = coolerRepository;
    }

    @GetMapping
    public List<Cooler> getCoolers() {
        return coolerRepository.findAll();
    }

    @GetMapping("/{coolerId}")
    public Cooler getCooler(@PathVariable Long coolerId) {
        return coolerRepository.findById(coolerId)
                .orElseThrow(() -> new RuntimeException("Cooler not found with id: " + coolerId));
    }

    @PostMapping
    public Cooler createCooler(@Valid @RequestBody Cooler cooler) {
        return coolerRepository.save(cooler);
    }
}
