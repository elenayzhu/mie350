package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Build;
import com.team15.partpicker.model.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    @PostMapping("/preferences/{preferenceId}/builds")
    @ResponseStatus(HttpStatus.CREATED)
    public Build createBuild(
            @PathVariable @NonNull Long preferenceId,
            @Valid @RequestBody @NonNull Build build
    ) {
        return recommendationService.createBuild(preferenceId, build);
    }

    @GetMapping("/preferences/{preferenceId}/builds")
    public List<Build> getBuildsForPreference(@PathVariable @NonNull Long preferenceId) {
        return recommendationService.getBuildsForPreference(preferenceId);
    }
}
