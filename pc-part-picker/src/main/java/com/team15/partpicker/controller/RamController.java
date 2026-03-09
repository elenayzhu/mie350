package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Ram;
import com.team15.partpicker.model.repository.RamRepository;
import org.springframework.lang.NonNull;
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
@RequestMapping("/rams")
public class RamController {

    private final RamRepository ramRepository;

    public RamController(RamRepository ramRepository) {
        this.ramRepository = ramRepository;
    }

    @GetMapping
    public List<Ram> getRams(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String ddrType,
            @RequestParam(required = false) Integer minSpeed,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return ramRepository.findAll().stream()
                .filter(ram -> brand == null || ram.getBrand().equalsIgnoreCase(brand))
                .filter(ram -> ddrType == null || ram.getDdrType().equalsIgnoreCase(ddrType))
                .filter(ram -> minSpeed == null || ram.getSpeedMhz() >= minSpeed)
                .filter(ram -> minCapacity == null || ram.getCapacityGb() >= minCapacity)
                .filter(ram -> minPrice == null || ram.getPrice().compareTo(minPrice) >= 0)
                .filter(ram -> maxPrice == null || ram.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @GetMapping("/{ramId}")
    public Ram getRam(@PathVariable @NonNull Long ramId) {
        return ramRepository.findById(ramId)
                .orElseThrow(() -> new RuntimeException("Ram not found with id: " + ramId));
    }

    @PostMapping
    public Ram createRam(@Valid @RequestBody @NonNull Ram ram) {
        return ramRepository.save(ram);
    }
}
