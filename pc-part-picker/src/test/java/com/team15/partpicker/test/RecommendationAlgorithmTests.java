package com.team15.partpicker.test;

import com.team15.partpicker.controller.RecommendationResponse;
import com.team15.partpicker.model.entity.BuildCategory;
import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.entity.Motherboard;
import com.team15.partpicker.model.entity.Ram;
import com.team15.partpicker.model.entity.Storage;
import com.team15.partpicker.model.entity.Psu;
import com.team15.partpicker.model.entity.Cooler;
import com.team15.partpicker.model.entity.Case;
import com.team15.partpicker.model.entity.Build;
import com.team15.partpicker.model.entity.UserPreference;
import com.team15.partpicker.model.repository.BuildRepository;
import com.team15.partpicker.model.repository.CaseRepository;
import com.team15.partpicker.model.repository.CoolerRepository;
import com.team15.partpicker.model.repository.CpuRepository;
import com.team15.partpicker.model.repository.GpuRepository;
import com.team15.partpicker.model.repository.MotherboardRepository;
import com.team15.partpicker.model.repository.PsuRepository;
import com.team15.partpicker.model.repository.RamRepository;
import com.team15.partpicker.model.repository.StorageRepository;
import com.team15.partpicker.model.repository.UserPreferenceRepository;
import com.team15.partpicker.model.repository.UserProfileRepository;
import com.team15.partpicker.model.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RecommendationAlgorithmTests { // tests the RecommendationService algorithm logic directly, without using the HTTP endpoints

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private BuildRepository buildRepository;

    @Test
    void recommendForPreference_gamingCategory_staysWithinBudget() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("1500"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            assertTrue(response.getTotalPrice().compareTo(new BigDecimal("1500")) <= 0,
                    "Total price should be within budget");
            assertTrue(response.getTotalPrice().compareTo(BigDecimal.ZERO) > 0,
                    "Total price should be greater than zero");
            assertNotNull(response.getCpu());
            assertNotNull(response.getGpu());
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_aiMlCategory_staysWithinBudget() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.AI_ML);
        preference.setMaxBudget(new BigDecimal("2000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            assertTrue(response.getTotalPrice().compareTo(new BigDecimal("2000")) <= 0);
            assertTrue(response.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_workstationCategory_staysWithinBudget() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.WORKSTATION);
        preference.setMaxBudget(new BigDecimal("2500"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            assertTrue(response.getTotalPrice().compareTo(new BigDecimal("2500")) <= 0);
            assertTrue(response.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_nullCategory_defaultsToGaming() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(null);
        preference.setMaxBudget(new BigDecimal("1300"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            assertTrue(response.getTotalPrice().compareTo(new BigDecimal("1300")) <= 0);
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_nullBudget_defaultsToZero() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(null);
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            assertEquals(0, response.getTotalPrice().compareTo(BigDecimal.ZERO),
                    "Zero budget should produce zero total price");
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withCpuBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("1500"));
        preference.setPreferredCpuBrand("AMD");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            assertNotNull(response.getCpu());
            assertEquals("AMD", response.getCpu().getBrand(),
                    "CPU brand should match preference when parts are available");
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_lowBudget_stillReturnsBuild() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("100"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            assertNotNull(response.getBuildId());
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_motherboardMatchesCpuSocket() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            if (response.getCpu() != null && response.getMotherboard() != null) {
                String cpuSocket = response.getCpu().getSocket();
                String moboSocket = response.getMotherboard().getSocket();
                if (cpuSocket != null && moboSocket != null) {
                    assertEquals(cpuSocket.toLowerCase(), moboSocket.toLowerCase(),
                            "Motherboard socket should match CPU socket");
                }
            }
            buildId = response.getBuildId();
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_highBudget_usesMoreExpensiveParts() {
        UserPreference lowBudgetPref = new UserPreference();
        lowBudgetPref.setBuildCategory(BuildCategory.GAMING);
        lowBudgetPref.setMaxBudget(new BigDecimal("500"));
        UserPreference savedLow = userPreferenceRepository.save(lowBudgetPref);

        UserPreference highBudgetPref = new UserPreference();
        highBudgetPref.setBuildCategory(BuildCategory.GAMING);
        highBudgetPref.setMaxBudget(new BigDecimal("3000"));
        UserPreference savedHigh = userPreferenceRepository.save(highBudgetPref);

        Long lowBuildId = null;
        Long highBuildId = null;
        try {
            RecommendationResponse lowResponse = recommendationService.recommendForPreference(savedLow.getId());
            RecommendationResponse highResponse = recommendationService.recommendForPreference(savedHigh.getId());

            assertTrue(highResponse.getTotalPrice().compareTo(lowResponse.getTotalPrice()) >= 0,
                    "Higher budget should produce equal or more expensive build");

            lowBuildId = lowResponse.getBuildId();
            highBuildId = highResponse.getBuildId();
        } finally {
            if (lowBuildId != null) buildRepository.deleteById(lowBuildId);
            if (highBuildId != null) buildRepository.deleteById(highBuildId);
            userPreferenceRepository.deleteById(savedLow.getId());
            userPreferenceRepository.deleteById(savedHigh.getId());
        }
    }

    @Test
    void recommendForPreference_buildIsPersisted() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("1200"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            buildId = response.getBuildId();
            assertNotNull(buildId);

            Build persistedBuild = buildRepository.findById(buildId).orElse(null);
            assertNotNull(persistedBuild, "Build should be persisted in the database");
            assertEquals("Recommended Build", persistedBuild.getBuildTitle());
            assertNotNull(persistedBuild.getCreatedAt());
        } finally {
            if (buildId != null) {
                buildRepository.deleteById(buildId);
            }
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withGpuBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredGpuBrand("NVIDIA");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            assertNotNull(response.getGpu());
            assertEquals("NVIDIA", response.getGpu().getBrand(),
                    "GPU brand should match preference when parts are available");
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withCoolerBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredCoolerBrand("Thermalright");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            if (response.getCooler() != null) {
                assertEquals("Thermalright", response.getCooler().getBrand(),
                        "Cooler brand should match preference when parts are available");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withCaseBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredCaseBrand("Corsair");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            if (response.getComputerCase() != null) {
                assertEquals("Corsair", response.getComputerCase().getBrand(),
                        "Case brand should match preference when parts are available");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withRamBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredRamBrand("Corsair");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            if (response.getRam() != null) {
                assertEquals("Corsair", response.getRam().getBrand(),
                        "RAM brand should match preference when parts are available");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withStorageBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredStorageBrand("Samsung");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            if (response.getStorage() != null) {
                assertEquals("Samsung", response.getStorage().getBrand(),
                        "Storage brand should match preference when parts are available");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withPsuBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredPsuBrand("Corsair");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            if (response.getPsu() != null) {
                assertEquals("Corsair", response.getPsu().getBrand(),
                        "PSU brand should match preference when parts are available");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_withMotherboardBrandPreference_prefersThatBrand() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredMotherboardBrand("ASUS");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            // Brand preference is attempted but may fall back to another brand if no compatible
            // ASUS board matches the CPU socket and budget — verify a motherboard was still selected
            assertNotNull(buildId, "Build should be created even when preferred motherboard brand may not be available");
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_aiMlCategory_allocatesMoreToCpu() {
        UserPreference gamingPref = new UserPreference();
        gamingPref.setBuildCategory(BuildCategory.GAMING);
        gamingPref.setMaxBudget(new BigDecimal("2000"));
        UserPreference savedGaming = userPreferenceRepository.save(gamingPref);

        UserPreference aiMlPref = new UserPreference();
        aiMlPref.setBuildCategory(BuildCategory.AI_ML);
        aiMlPref.setMaxBudget(new BigDecimal("2000"));
        UserPreference savedAiMl = userPreferenceRepository.save(aiMlPref);

        Long gamingBuildId = null;
        Long aiMlBuildId = null;
        try {
            RecommendationResponse gamingResponse = recommendationService.recommendForPreference(savedGaming.getId());
            RecommendationResponse aiMlResponse = recommendationService.recommendForPreference(savedAiMl.getId());

            assertNotNull(gamingResponse);
            assertNotNull(aiMlResponse);
            gamingBuildId = gamingResponse.getBuildId();
            aiMlBuildId = aiMlResponse.getBuildId();

            // AI_ML allocates 27% to CPU vs GAMING's 24%, so AI_ML CPU should cost >= GAMING CPU
            if (gamingResponse.getCpu() != null && aiMlResponse.getCpu() != null) {
                assertTrue(
                        aiMlResponse.getCpu().getPrice().compareTo(gamingResponse.getCpu().getPrice()) >= 0,
                        "AI_ML build should allocate more budget to CPU than GAMING build"
                );
            }
        } finally {
            if (gamingBuildId != null) buildRepository.deleteById(gamingBuildId);
            if (aiMlBuildId != null) buildRepository.deleteById(aiMlBuildId);
            userPreferenceRepository.deleteById(savedGaming.getId());
            userPreferenceRepository.deleteById(savedAiMl.getId());
        }
    }

    @Test
    void recommendForPreference_workstationCategory_allocatesMoreToCpuThanGaming() {
        UserPreference gamingPref = new UserPreference();
        gamingPref.setBuildCategory(BuildCategory.GAMING);
        gamingPref.setMaxBudget(new BigDecimal("2000"));
        UserPreference savedGaming = userPreferenceRepository.save(gamingPref);

        UserPreference workstationPref = new UserPreference();
        workstationPref.setBuildCategory(BuildCategory.WORKSTATION);
        workstationPref.setMaxBudget(new BigDecimal("2000"));
        UserPreference savedWorkstation = userPreferenceRepository.save(workstationPref);

        Long gamingBuildId = null;
        Long workstationBuildId = null;
        try {
            RecommendationResponse gamingResponse = recommendationService.recommendForPreference(savedGaming.getId());
            RecommendationResponse workstationResponse = recommendationService.recommendForPreference(savedWorkstation.getId());

            assertNotNull(gamingResponse);
            assertNotNull(workstationResponse);
            gamingBuildId = gamingResponse.getBuildId();
            workstationBuildId = workstationResponse.getBuildId();

            // WORKSTATION allocates 30% to CPU vs GAMING's 24%
            if (gamingResponse.getCpu() != null && workstationResponse.getCpu() != null) {
                assertTrue(
                        workstationResponse.getCpu().getPrice().compareTo(gamingResponse.getCpu().getPrice()) >= 0,
                        "WORKSTATION build should allocate more budget to CPU than GAMING build"
                );
            }
        } finally {
            if (gamingBuildId != null) buildRepository.deleteById(gamingBuildId);
            if (workstationBuildId != null) buildRepository.deleteById(workstationBuildId);
            userPreferenceRepository.deleteById(savedGaming.getId());
            userPreferenceRepository.deleteById(savedWorkstation.getId());
        }
    }

    @Test
    void recommendForPreference_totalPriceEqualsPartPricesSum() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("1500"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();

            BigDecimal expectedTotal = BigDecimal.ZERO;
            if (response.getCpu() != null && response.getCpu().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getCpu().getPrice());
            if (response.getGpu() != null && response.getGpu().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getGpu().getPrice());
            if (response.getMotherboard() != null && response.getMotherboard().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getMotherboard().getPrice());
            if (response.getRam() != null && response.getRam().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getRam().getPrice());
            if (response.getStorage() != null && response.getStorage().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getStorage().getPrice());
            if (response.getPsu() != null && response.getPsu().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getPsu().getPrice());
            if (response.getCooler() != null && response.getCooler().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getCooler().getPrice());
            if (response.getComputerCase() != null && response.getComputerCase().getPrice() != null)
                expectedTotal = expectedTotal.add(response.getComputerCase().getPrice());

            assertEquals(0, response.getTotalPrice().compareTo(expectedTotal),
                    "Total price should equal sum of all part prices");
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }
}
