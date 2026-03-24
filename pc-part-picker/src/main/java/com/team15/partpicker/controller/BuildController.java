package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Build;
import com.team15.partpicker.model.dto.CreateBuildRequest;
import com.team15.partpicker.model.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/builds")
    public List<Build> getAllBuilds() {
        return recommendationService.getAllBuilds();
    }

    @GetMapping("/profiles/{profileId}/builds")
    public List<Build> getBuildsForUserProfile(@PathVariable @NonNull Long profileId) {
        return recommendationService.getBuildsForUserProfile(profileId);
    }

    @GetMapping("/builds/{buildId}")
    public Build getBuild(@PathVariable @NonNull Long buildId) {
        return recommendationService.getBuild(buildId);
    }

    @PostMapping("/preferences/{preferenceId}/builds")
    @ResponseStatus(HttpStatus.CREATED)
    public Build createBuild(
            @PathVariable @NonNull Long preferenceId,
            @Valid @RequestBody @NonNull CreateBuildRequest request
    ) {
        return recommendationService.createBuild(preferenceId, request.getBuildTitle());
    }

    @PostMapping("/profiles/{profileId}/preferences/{preferenceId}/builds")
    @ResponseStatus(HttpStatus.CREATED)
    public Build createBuildForUserProfile(
            @PathVariable @NonNull Long profileId,
            @PathVariable @NonNull Long preferenceId,
            @Valid @RequestBody @NonNull CreateBuildRequest request
    ) {
        return recommendationService.createBuildForUserProfile(profileId, preferenceId, request.getBuildTitle());
    }

    @GetMapping("/preferences/{preferenceId}/builds")
    public List<Build> getBuildsForPreference(@PathVariable @NonNull Long preferenceId) {
        return recommendationService.getBuildsForPreference(preferenceId);
    }

    @DeleteMapping("/builds/{buildId}")
    public ResponseEntity<Void> deleteBuild(@PathVariable @NonNull Long buildId) {
        recommendationService.deleteBuild(buildId);
        return ResponseEntity.noContent().build();
    }
}
