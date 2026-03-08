package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.service.RecommendationService;
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
@RequestMapping("/gpus")
public class GpuController {

    private final RecommendationService recommendationService;

    public GpuController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<Gpu> getGpus(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer minVramGb,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return recommendationService.listAllGpus().stream()
                .filter(gpu -> brand == null || gpu.getBrand().equalsIgnoreCase(brand))
                .filter(gpu -> minVramGb == null || gpu.getVramGb() >= minVramGb)
                .filter(gpu -> minPrice == null || gpu.getPrice().compareTo(minPrice) >= 0)
                .filter(gpu -> maxPrice == null || gpu.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @GetMapping("/{gpuId}")
    public Gpu getGpu(@PathVariable Long gpuId) {
        return recommendationService.getGpu(gpuId);
    }

    @PostMapping
    public Gpu createGpu(@Valid @RequestBody Gpu gpu) {
        return recommendationService.addGpu(gpu);
    }
}
