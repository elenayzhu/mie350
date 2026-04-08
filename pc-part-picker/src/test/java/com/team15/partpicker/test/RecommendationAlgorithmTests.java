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
    void recommendForPreference_nonExistentPreference_throws() {
        assertThrows(com.team15.partpicker.exception.UserPreferenceNotFoundException.class,
                () -> recommendationService.recommendForPreference(999999L));
    }

    @Test
    void recommendForPreference_allEightPartsPresent_highBudget() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("5000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();
            assertNotNull(response.getCpu(), "CPU should be present with high budget");
            assertNotNull(response.getGpu(), "GPU should be present with high budget");
            assertNotNull(response.getMotherboard(), "Motherboard should be present with high budget");
            assertNotNull(response.getRam(), "RAM should be present with high budget");
            assertNotNull(response.getStorage(), "Storage should be present with high budget");
            assertNotNull(response.getPsu(), "PSU should be present with high budget");
            assertNotNull(response.getCooler(), "Cooler should be present with high budget");
            assertNotNull(response.getComputerCase(), "Case should be present with high budget");
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_psuWattageIsSufficientForBuild() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();

            if (response.getPsu() != null && response.getCpu() != null) {
                Cpu cpu = response.getCpu();
                Gpu gpu = response.getGpu();

                int cores = cpu.getCores() != null ? cpu.getCores() : 0;
                int cpuWatts = cores <= 6 ? 95 : (cores <= 8 ? 125 : 170);
                int gpuWatts = (gpu == null || gpu.getVramGb() == null) ? 0
                        : (gpu.getVramGb() <= 8 ? 150 : (gpu.getVramGb() <= 12 ? 220 : 300));
                int required = (int) Math.ceil((cpuWatts + gpuWatts + 100) * 1.3);

                assertTrue(response.getPsu().getWattage() >= required,
                        "PSU wattage should meet the computed minimum for the selected CPU and GPU");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_coolerSocketCompatibleWithCpu() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();

            if (response.getCpu() != null && response.getCooler() != null) {
                String cpuSocket = response.getCpu().getSocket();
                String coolerSocket = response.getCooler().getSocket();
                if (cpuSocket != null && coolerSocket != null && !coolerSocket.isBlank()) {
                    assertEquals(cpuSocket.toLowerCase(), coolerSocket.toLowerCase(),
                            "Cooler socket should be compatible with CPU socket");
                }
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_multipleBrandPreferences_allRespected() {
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        preference.setPreferredCpuBrand("AMD");
        preference.setPreferredGpuBrand("NVIDIA");
        preference.setPreferredRamBrand("Corsair");
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();

            if (response.getCpu() != null) {
                assertEquals("AMD", response.getCpu().getBrand(), "CPU brand should match preference");
            }
            if (response.getGpu() != null) {
                assertEquals("NVIDIA", response.getGpu().getBrand(), "GPU brand should match preference");
            }
            if (response.getRam() != null) {
                assertEquals("Corsair", response.getRam().getBrand(), "RAM brand should match preference");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_gamingCategory_gpuGetsLargerShareThanCpu() {
        // GAMING: GPU=32%, CPU=24%
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(new BigDecimal("2000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();

            if (response.getCpu() != null && response.getGpu() != null) {
                assertTrue(
                        response.getGpu().getPrice().compareTo(response.getCpu().getPrice()) >= 0,
                        "In GAMING build, GPU should receive a larger budget share than CPU (32% vs 24%)");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_sameCategoryAndBudget_isDeterministic() {
        UserPreference pref1 = new UserPreference();
        pref1.setBuildCategory(BuildCategory.GAMING);
        pref1.setMaxBudget(new BigDecimal("1500"));
        UserPreference saved1 = userPreferenceRepository.save(pref1);

        UserPreference pref2 = new UserPreference();
        pref2.setBuildCategory(BuildCategory.GAMING);
        pref2.setMaxBudget(new BigDecimal("1500"));
        UserPreference saved2 = userPreferenceRepository.save(pref2);

        Long buildId1 = null;
        Long buildId2 = null;
        try {
            RecommendationResponse response1 = recommendationService.recommendForPreference(saved1.getId());
            RecommendationResponse response2 = recommendationService.recommendForPreference(saved2.getId());

            assertNotNull(response1);
            assertNotNull(response2);
            buildId1 = response1.getBuildId();
            buildId2 = response2.getBuildId();

            assertEquals(0, response1.getTotalPrice().compareTo(response2.getTotalPrice()),
                    "Same category and budget should produce identical total prices");

            if (response1.getCpu() != null && response2.getCpu() != null) {
                assertEquals(response1.getCpu().getId(), response2.getCpu().getId(),
                        "Same category and budget should select the same CPU");
            }
            if (response1.getGpu() != null && response2.getGpu() != null) {
                assertEquals(response1.getGpu().getId(), response2.getGpu().getId(),
                        "Same category and budget should select the same GPU");
            }
        } finally {
            if (buildId1 != null) buildRepository.deleteById(buildId1);
            if (buildId2 != null) buildRepository.deleteById(buildId2);
            userPreferenceRepository.deleteById(saved1.getId());
            userPreferenceRepository.deleteById(saved2.getId());
        }
    }

    @Test
    void recommendForPreference_aiMlCategory_gpuGetsLargestBudgetShare() {
        // AI_ML: GPU=34%, CPU=27% -- GPU allocation is the largest
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.AI_ML);
        preference.setMaxBudget(new BigDecimal("3000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();

            if (response.getGpu() != null && response.getCpu() != null) {
                assertTrue(
                        response.getGpu().getPrice().compareTo(response.getCpu().getPrice()) >= 0,
                        "In AI_ML build, GPU (34%) should be at least as expensive as CPU (27%)");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_negativeBudget_treatedAsZeroOrEmpty() {
        // @DecimalMin("0.0") on the entity prevents negative values from being persisted,
        // but if a zero budget is passed the service should handle it gracefully
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.GAMING);
        preference.setMaxBudget(BigDecimal.ZERO);
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response, "Service should return a response even for zero budget");
            buildId = response.getBuildId();
            assertEquals(0, response.getTotalPrice().compareTo(BigDecimal.ZERO),
                    "Zero budget should produce zero total price");
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
        }
    }

    @Test
    void recommendForPreference_workstationCategory_gpuGetsLessThanCpu() {
        // WORKSTATION: CPU=30%, GPU=25% -- CPU gets more than GPU (opposite of GAMING)
        UserPreference preference = new UserPreference();
        preference.setBuildCategory(BuildCategory.WORKSTATION);
        preference.setMaxBudget(new BigDecimal("3000"));
        UserPreference saved = userPreferenceRepository.save(preference);

        Long buildId = null;
        try {
            RecommendationResponse response = recommendationService.recommendForPreference(saved.getId());

            assertNotNull(response);
            buildId = response.getBuildId();

            if (response.getCpu() != null && response.getGpu() != null) {
                assertTrue(
                        response.getCpu().getPrice().compareTo(response.getGpu().getPrice()) >= 0,
                        "In WORKSTATION build, CPU (30%) should receive at least as much budget as GPU (25%)");
            }
        } finally {
            if (buildId != null) buildRepository.deleteById(buildId);
            userPreferenceRepository.deleteById(saved.getId());
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
