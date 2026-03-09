package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Psu;
import com.team15.partpicker.model.repository.PsuRepository;
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
@RequestMapping("/psus")
public class PsuController {

    private final PsuRepository psuRepository;

    public PsuController(PsuRepository psuRepository) {
        this.psuRepository = psuRepository;
    }

    @GetMapping
    public List<Psu> getPsus(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer minWattage,
            @RequestParam(required = false) String efficiencyRating,
            @RequestParam(required = false) String modularType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return psuRepository.findAll().stream()
                .filter(psu -> brand == null || psu.getBrand().equalsIgnoreCase(brand))
                .filter(psu -> minWattage == null || psu.getWattage() >= minWattage)
                .filter(psu -> efficiencyRating == null || psu.getEfficiencyRating().equalsIgnoreCase(efficiencyRating))
                .filter(psu -> modularType == null || psu.getModularType().equalsIgnoreCase(modularType))
                .filter(psu -> minPrice == null || psu.getPrice().compareTo(minPrice) >= 0)
                .filter(psu -> maxPrice == null || psu.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @GetMapping("/{psuId}")
    public Psu getPsu(@PathVariable @NonNull Long psuId) {
        return psuRepository.findById(psuId)
                .orElseThrow(() -> new RuntimeException("PSU not found with id: " + psuId));
    }

    @PostMapping
    public Psu createPsu(@Valid @RequestBody @NonNull Psu psu) {
        return psuRepository.save(psu);
    }
}
