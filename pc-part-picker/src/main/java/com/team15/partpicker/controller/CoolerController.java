package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Cooler;
import com.team15.partpicker.model.repository.CoolerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/coolers")
public class CoolerController {

    private final CoolerRepository coolerRepository;

    public CoolerController(CoolerRepository coolerRepository) {
        this.coolerRepository = coolerRepository;
    }

    @GetMapping
    public List<Cooler> getCoolers(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String socket,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minMaxTdp,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return coolerRepository.findAll().stream()
                .filter(cooler -> brand == null || cooler.getBrand().equalsIgnoreCase(brand))
                .filter(cooler -> socket == null || cooler.getSocket().equalsIgnoreCase(socket))
                .filter(cooler -> type == null || cooler.getType().equalsIgnoreCase(type))
                .filter(cooler -> minMaxTdp == null || cooler.getMaxTdp() >= minMaxTdp)
                .filter(cooler -> minPrice == null || cooler.getPrice().compareTo(minPrice) >= 0)
                .filter(cooler -> maxPrice == null || cooler.getPrice().compareTo(maxPrice) <= 0)
                .toList();
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
