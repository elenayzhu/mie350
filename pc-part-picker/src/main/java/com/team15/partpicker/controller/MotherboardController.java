package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Motherboard;
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
@RequestMapping("/motherboards")
public class MotherboardController {

    private final RecommendationService recommendationService;

    public MotherboardController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<Motherboard> getMotherboards(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String socket,
            @RequestParam(required = false) String formFactor,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return recommendationService.listAllMotherboards().stream()
                .filter(mobo -> brand == null || mobo.getBrand().equalsIgnoreCase(brand))
                .filter(mobo -> socket == null || mobo.getSocket().equalsIgnoreCase(socket))
                .filter(mobo -> formFactor == null || mobo.getFormFactor().equalsIgnoreCase(formFactor))
                .filter(mobo -> minPrice == null || mobo.getPrice().compareTo(minPrice) >= 0)
                .filter(mobo -> maxPrice == null || mobo.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @GetMapping("/{motherboardId}")
    public Motherboard getMotherboard(@PathVariable @NonNull Long motherboardId) {
        return recommendationService.getMotherboard(motherboardId);
    }

    @PostMapping
    public Motherboard createMotherboard(@Valid @RequestBody @NonNull Motherboard motherboard) {
        return recommendationService.addMotherboard(motherboard);
    }
}
