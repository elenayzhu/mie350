package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.UserProfile;
import com.team15.partpicker.model.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/profiles")
public class UserProfileController {

    private final RecommendationService recommendationService;

    public UserProfileController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfile createUserProfile(@Valid @RequestBody @NonNull UserProfile userProfile) {
        return recommendationService.createUserProfile(userProfile);
    }

    @GetMapping("/user/{userId}")
    public UserProfile getUserProfile(@PathVariable @NonNull Long userId) {
        return recommendationService.getUserProfile(userId);
    }

    @PutMapping("/{profileId}")
    public UserProfile updateUserProfile(
            @PathVariable @NonNull Long profileId,
            @Valid @RequestBody @NonNull UserProfile userProfile
    ) {
        return recommendationService.updateUserProfile(profileId, userProfile);
    }
}
