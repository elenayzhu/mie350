package com.team15.partpicker.test;

import com.team15.partpicker.controller.RecommendationResponse;
import com.team15.partpicker.exception.CpuNotFoundException;
import com.team15.partpicker.exception.GpuNotFoundException;
import com.team15.partpicker.exception.InvalidLoginException;
import com.team15.partpicker.exception.MotherboardNotFoundException;
import com.team15.partpicker.exception.UserPreferenceNotFoundException;
import com.team15.partpicker.model.entity.Build;
import com.team15.partpicker.model.entity.BuildCategory;
import com.team15.partpicker.model.entity.Cooler;
import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.entity.Motherboard;
import com.team15.partpicker.model.entity.Psu;
import com.team15.partpicker.model.entity.UserPreference;
import com.team15.partpicker.model.entity.UserProfile;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationServiceUnitTests {

    @Mock private CpuRepository cpuRepository;
    @Mock private GpuRepository gpuRepository;
    @Mock private MotherboardRepository motherboardRepository;
    @Mock private RamRepository ramRepository;
    @Mock private StorageRepository storageRepository;
    @Mock private PsuRepository psuRepository;
    @Mock private CoolerRepository coolerRepository;
    @Mock private CaseRepository caseRepository;
    @Mock private UserPreferenceRepository userPreferenceRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private BuildRepository buildRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private void stubBuildSave(UserPreference preference) {
        when(buildRepository.save(any(Build.class))).thenAnswer(inv -> {
            Build b = inv.getArgument(0);
            b.setId(99L);
            b.setUserPreference(preference);
            return b;
        });
    }

    @Test
    void recommendForPreference_preferenceNotFound_throws() {
        when(userPreferenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserPreferenceNotFoundException.class,
                () -> recommendationService.recommendForPreference(999L));
    }

    @Test
    void recommendForPreference_zeroBudget_returnsEmptyBuildWithZeroTotal() {
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(BigDecimal.ZERO);

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response);
        assertEquals(0, response.getTotalPrice().compareTo(BigDecimal.ZERO),
                "Zero budget should produce zero total price");
        assertNull(response.getCpu(), "Zero budget should produce no CPU");
        assertNull(response.getGpu(), "Zero budget should produce no GPU");
    }

    @Test
    void recommendForPreference_cpuBrandFallbackWhenPreferredUnavailable() {
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(new BigDecimal("1500"));
        pref.setPreferredCpuBrand("FAKEBRAND");

        Cpu fallbackCpu = new Cpu(1L, "Fallback CPU", "AMD", "AM5", 8, 125, new BigDecimal("300"));

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(cpuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(eq("FAKEBRAND"), any(BigDecimal.class)))
                .thenReturn(List.of());
        when(cpuRepository.findByPriceLessThanEqual(any(BigDecimal.class)))
                .thenReturn(List.of(fallbackCpu));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response.getCpu(),
                "Should fall back to any-brand CPU when preferred brand is unavailable");
        assertEquals("AMD", response.getCpu().getBrand());
    }

    @Test
    void recommendForPreference_gpuBrandFallbackWhenPreferredUnavailable() {
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(new BigDecimal("1500"));
        pref.setPreferredGpuBrand("FAKEGPUBRAND");

        Gpu fallbackGpu = new Gpu(1L, "Fallback GPU", "NVIDIA", null, null, 8, null, null, new BigDecimal("400"));

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(gpuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(eq("FAKEGPUBRAND"), any(BigDecimal.class)))
                .thenReturn(List.of());
        when(gpuRepository.findByPriceLessThanEqual(any(BigDecimal.class)))
                .thenReturn(List.of(fallbackGpu));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response.getGpu(),
                "Should fall back to any-brand GPU when preferred brand is unavailable");
        assertEquals("NVIDIA", response.getGpu().getBrand());
    }

    @Test
    void recommendForPreference_nullCpuSocket_motherboardNotFilteredBySocket() {
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(new BigDecimal("1500"));

        Cpu cpuNoSocket = new Cpu(1L, "Socket-less CPU", "AMD", null, 8, 125, new BigDecimal("200"));
        Motherboard anyMobo = new Motherboard(
                1L, "Generic Board", "ASUS", "DDR5", "AM5", "ATX", "Black", 4, new BigDecimal("150"));

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(cpuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(cpuNoSocket));
        when(motherboardRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(anyMobo));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response.getCpu());
        assertNull(response.getCpu().getSocket(), "CPU should have null socket as configured");
        assertNotNull(response.getMotherboard(),
                "Motherboard should still be selected when CPU socket is null (no socket filter applied)");
    }

    @Test
    void choosePsu_respectsMinimumWattageRequirement() {
        // CPU 8 cores -> 125W, GPU 12 VRAM -> 220W
        // Required PSU = ceil((125 + 220 + 100) * 1.3) = ceil(579.0) = 579
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(new BigDecimal("1500"));

        Cpu cpu = new Cpu(1L, "Test CPU", "AMD", "AM5", 8, 125, new BigDecimal("200"));
        Gpu gpu = new Gpu(1L, "Test GPU", "NVIDIA", null, null, 12, null, null, new BigDecimal("400"));
        Psu weakPsu = new Psu(1L, "Weak PSU", "Generic", 500, "80+ Bronze", "Non-Modular", "Black", new BigDecimal("60"));
        Psu strongPsu = new Psu(2L, "Strong PSU", "Corsair", 700, "80+ Gold", "Fully Modular", "Black", new BigDecimal("120"));

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(cpuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(cpu));
        when(gpuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(gpu));
        when(psuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(weakPsu, strongPsu));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response.getPsu(), "A sufficient PSU should be selected");
        assertTrue(response.getPsu().getWattage() >= 579,
                "Selected PSU wattage should meet the minimum 579W threshold");
        assertEquals(700, response.getPsu().getWattage(),
                "Should select the 700W PSU since 500W is below the required threshold");
    }

    @Test
    void choosePsu_filtersOutUnderpoweredPsus() {
        // Required PSU >= 579W (same as test above)
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(new BigDecimal("1500"));

        Cpu cpu = new Cpu(1L, "Test CPU", "AMD", "AM5", 8, 125, new BigDecimal("200"));
        Gpu gpu = new Gpu(1L, "Test GPU", "NVIDIA", null, null, 12, null, null, new BigDecimal("400"));
        Psu underpoweredPsu = new Psu(1L, "Underpowered PSU", "Generic", 400, "80+ Bronze", "Non-Modular", "Black", new BigDecimal("40"));

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(cpuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(cpu));
        when(gpuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(gpu));
        when(psuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(underpoweredPsu));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNull(response.getPsu(),
                "PSU candidates below required wattage should be filtered out, leaving no PSU selected");
    }

    @Test
    void chooseCooler_respectsSocketCompatibility() {
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(new BigDecimal("1500"));

        Cpu cpu = new Cpu(1L, "Test CPU", "AMD", "AM5", 8, 125, new BigDecimal("200"));
        Cooler incompatibleCooler = new Cooler(1L, "LGA Cooler", "Noctua", "LGA1700", 170, "Air", "Black", new BigDecimal("50"));
        Cooler compatibleCooler = new Cooler(2L, "AM5 Cooler", "Thermalright", "AM5", 170, "Air", "White", new BigDecimal("60"));

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(cpuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(cpu));
        when(coolerRepository.findByPriceLessThanEqual(any(BigDecimal.class)))
                .thenReturn(List.of(incompatibleCooler, compatibleCooler));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response.getCooler(), "A compatible cooler should be selected");
        assertEquals("AM5", response.getCooler().getSocket(),
                "Should select only the cooler whose socket matches the CPU socket");
    }

    @Test
    void chooseCooler_nullSocketCpuAllowsAnyCooler() {
        UserPreference pref = new UserPreference();
        pref.setId(1L);
        pref.setBuildCategory(BuildCategory.GAMING);
        pref.setMaxBudget(new BigDecimal("1500"));

        Cpu cpuNoSocket = new Cpu(1L, "Socket-less CPU", "AMD", null, 8, 125, new BigDecimal("200"));
        Cooler lgaCooler = new Cooler(1L, "LGA Cooler", "Noctua", "LGA1700", 170, "Air", "Black", new BigDecimal("50"));

        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(cpuRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(cpuNoSocket));
        when(coolerRepository.findByPriceLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(lgaCooler));
        stubBuildSave(pref);

        RecommendationResponse response = recommendationService.recommendForPreference(1L);

        assertNotNull(response.getCooler(),
                "Any cooler should be compatible when the CPU has no socket requirement");
    }

    @Test
    void getCpu_notFound_throwsCpuNotFoundException() {
        when(cpuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CpuNotFoundException.class,
                () -> recommendationService.getCpu(999L));
    }

    @Test
    void getGpu_notFound_throwsGpuNotFoundException() {
        when(gpuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GpuNotFoundException.class,
                () -> recommendationService.getGpu(999L));
    }

    @Test
    void getMotherboard_notFound_throwsMotherboardNotFoundException() {
        when(motherboardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MotherboardNotFoundException.class,
                () -> recommendationService.getMotherboard(999L));
    }

    @Test
    void login_blankEmail_throwsInvalidLoginException() {
        assertThrows(InvalidLoginException.class,
                () -> recommendationService.login("", "password"));
    }

    @Test
    void login_correctCredentials_returnsProfile() {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setEmail("test@test.com");
        profile.setPassword("secret123");
        profile.setFirstName("Test");
        profile.setLastName("User");

        when(userProfileRepository.findByEmailIgnoreCase("test@test.com")).thenReturn(Optional.of(profile));

        UserProfile result = recommendationService.login("test@test.com", "secret123");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getEmail());
    }
}
