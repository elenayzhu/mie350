package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/gpus")
public class GpuController {

    private final RecommendationService recommendationService;

    public GpuController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<Gpu> getGpus() {
        return recommendationService.listAllGpus();
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
