package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.service.RecommendationService;
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
@RequestMapping("/cpus")
public class CpuController {

    private final RecommendationService recommendationService;

    public CpuController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<Cpu> getCpus(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String socket,
            @RequestParam(required = false) Integer minCores,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return recommendationService.listAllCpus().stream()
                .filter(cpu -> brand == null || cpu.getBrand().equalsIgnoreCase(brand))
                .filter(cpu -> socket == null || cpu.getSocket().equalsIgnoreCase(socket))
                .filter(cpu -> minCores == null || cpu.getCores() >= minCores)
                .filter(cpu -> minPrice == null || cpu.getPrice().compareTo(minPrice) >= 0)
                .filter(cpu -> maxPrice == null || cpu.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @GetMapping("/{cpuId}")
    public Cpu getCpu(@PathVariable @NonNull Long cpuId) {
        return recommendationService.getCpu(cpuId);
    }

    @PostMapping
    public Cpu createCpu(@Valid @RequestBody @NonNull Cpu cpu) {
        return recommendationService.addCpu(cpu);
    }
}
