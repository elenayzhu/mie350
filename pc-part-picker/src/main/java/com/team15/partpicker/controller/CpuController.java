package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Cpu;
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
@RequestMapping("/cpus")
public class CpuController {

    private final RecommendationService recommendationService;

    public CpuController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<Cpu> getCpus() {
        return recommendationService.listAllCpus();
    }

    @GetMapping("/{cpuId}")
    public Cpu getCpu(@PathVariable Long cpuId) {
        return recommendationService.getCpu(cpuId);
    }

    @PostMapping
    public Cpu createCpu(@Valid @RequestBody Cpu cpu) {
        return recommendationService.addCpu(cpu);
    }
}
