package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.UserPreference;
import com.team15.partpicker.model.service.RecommendationService;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/preferences")
    public UserPreference createPreference(@Valid @RequestBody @NonNull UserPreference userPreference) {
        return recommendationService.createPreference(userPreference);
    }

    @GetMapping("/preferences/{preferenceId}")
    public UserPreference getPreference(@PathVariable @NonNull Long preferenceId) {
        return recommendationService.getPreference(preferenceId);
    }

    @GetMapping("/recommendations/{preferenceId}")
    public RecommendationResponse getRecommendations(@PathVariable @NonNull Long preferenceId) {
        return recommendationService.recommendForPreference(preferenceId);
    }
}
