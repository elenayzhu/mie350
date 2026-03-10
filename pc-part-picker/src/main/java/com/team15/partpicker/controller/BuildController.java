package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Build;
import com.team15.partpicker.model.service.RecommendationService;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class BuildController {

    private final RecommendationService recommendationService;

    public BuildController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/builds/{buildId}")
    public Build getBuild(@PathVariable @NonNull Long buildId) {
        return recommendationService.getBuild(buildId);
    }

    @GetMapping("/preferences/{preferenceId}/builds")
    public List<Build> getBuildsForPreference(@PathVariable @NonNull Long preferenceId) {
        return recommendationService.getBuildsForPreference(preferenceId);
    }
}
