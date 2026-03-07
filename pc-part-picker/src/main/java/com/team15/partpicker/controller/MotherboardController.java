package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Motherboard;
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
@RequestMapping("/motherboards")
public class MotherboardController {

    private final RecommendationService recommendationService;

    public MotherboardController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<Motherboard> getMotherboards() {
        return recommendationService.listAllMotherboards();
    }

    @GetMapping("/{motherboardId}")
    public Motherboard getMotherboard(@PathVariable Long motherboardId) {
        return recommendationService.getMotherboard(motherboardId);
    }

    @PostMapping
    public Motherboard createMotherboard(@Valid @RequestBody Motherboard motherboard) {
        return recommendationService.addMotherboard(motherboard);
    }
}
