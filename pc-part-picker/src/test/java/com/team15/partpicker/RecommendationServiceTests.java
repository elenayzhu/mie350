package com.team15.partpicker;

import com.team15.partpicker.controller.RecommendationResponse;
import com.team15.partpicker.model.entity.UserPreference;
import com.team15.partpicker.model.repository.UserPreferenceRepository;
import com.team15.partpicker.model.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RecommendationServiceTests {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Test
    void seededPreferenceProducesFullRecommendationWithinBudget() {
        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response.getCpu());
        assertNotNull(response.getGpu());
        assertNotNull(response.getMotherboard());
        assertTrue(response.getTotalPrice().compareTo(new BigDecimal("1300.00")) <= 0);
    }

    @Test
    void lowBudgetStillBuildsPartialRecommendationWhenPossible() {
        UserPreference preference = new UserPreference();
        preference.setPreferredCpuBrand("AMD");
        preference.setPreferredGpuBrand("NVIDIA");
        preference.setPreferredMotherboardBrand("MSI");
        preference.setMaxBudget(new BigDecimal("600.00"));

        Long preferenceId = userPreferenceRepository.save(preference).getId();
        RecommendationResponse response = recommendationService.recommendForPreference(preferenceId);

        assertNotNull(response.getCpu());
        assertNotNull(response.getMotherboard());
        assertTrue(response.getTotalPrice().compareTo(new BigDecimal("600.00")) <= 0);
    }
}
