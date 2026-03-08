package com.team15.partpicker.model.service;

import com.team15.partpicker.controller.RecommendationResponse;
import com.team15.partpicker.exception.CpuNotFoundException;
import com.team15.partpicker.exception.GpuNotFoundException;
import com.team15.partpicker.exception.MotherboardNotFoundException;
import com.team15.partpicker.exception.UserPreferenceNotFoundException;
import com.team15.partpicker.model.entity.BuildCategory;
import com.team15.partpicker.model.entity.Case;
import com.team15.partpicker.model.entity.Cooler;
import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.entity.Motherboard;
import com.team15.partpicker.model.entity.Psu;
import com.team15.partpicker.model.entity.Ram;
import com.team15.partpicker.model.entity.Storage;
import com.team15.partpicker.model.entity.UserPreference;
import com.team15.partpicker.model.repository.CaseRepository;
import com.team15.partpicker.model.repository.CoolerRepository;
import com.team15.partpicker.model.repository.CpuRepository;
import com.team15.partpicker.model.repository.GpuRepository;
import com.team15.partpicker.model.repository.MotherboardRepository;
import com.team15.partpicker.model.repository.PsuRepository;
import com.team15.partpicker.model.repository.RamRepository;
import com.team15.partpicker.model.repository.StorageRepository;
import com.team15.partpicker.model.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RecommendationService {

    private final CpuRepository cpuRepository;
    private final GpuRepository gpuRepository;
    private final MotherboardRepository motherboardRepository;
    private final RamRepository ramRepository;
    private final PsuRepository psuRepository;
    private final CaseRepository caseRepository;
    private final StorageRepository storageRepository;
    private final CoolerRepository coolerRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public RecommendationService(
            CpuRepository cpuRepository,
            GpuRepository gpuRepository,
            MotherboardRepository motherboardRepository,
            RamRepository ramRepository,
            PsuRepository psuRepository,
            CaseRepository caseRepository,
            StorageRepository storageRepository,
            CoolerRepository coolerRepository,
            UserPreferenceRepository userPreferenceRepository
    ) {
        this.cpuRepository = cpuRepository;
        this.gpuRepository = gpuRepository;
        this.motherboardRepository = motherboardRepository;
        this.ramRepository = ramRepository;
        this.psuRepository = psuRepository;
        this.caseRepository = caseRepository;
        this.storageRepository = storageRepository;
        this.coolerRepository = coolerRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public List<Cpu> listAllCpus() {
        return cpuRepository.findAll();
    }

    public Cpu getCpu(Long cpuId) {
        return cpuRepository.findById(cpuId)
                .orElseThrow(() -> new CpuNotFoundException(cpuId));
    }

    public Cpu addCpu(Cpu cpu) {
        return cpuRepository.save(cpu);
    }

    public List<Gpu> listAllGpus() {
        return gpuRepository.findAll();
    }

    public Gpu getGpu(Long gpuId) {
        return gpuRepository.findById(gpuId)
                .orElseThrow(() -> new GpuNotFoundException(gpuId));
    }

    public Gpu addGpu(Gpu gpu) {
        return gpuRepository.save(gpu);
    }

    public List<Motherboard> listAllMotherboards() {
        return motherboardRepository.findAll();
    }

    public Motherboard getMotherboard(Long motherboardId) {
        return motherboardRepository.findById(motherboardId)
                .orElseThrow(() -> new MotherboardNotFoundException(motherboardId));
    }

    public Motherboard addMotherboard(Motherboard motherboard) {
        return motherboardRepository.save(motherboard);
    }

    public UserPreference createPreference(UserPreference userPreference) {
        return userPreferenceRepository.save(userPreference);
    }

    public UserPreference getPreference(Long preferenceId) {
        return userPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new UserPreferenceNotFoundException(preferenceId));
    }

    public RecommendationResponse recommendForPreference(Long preferenceId) {
        UserPreference preference = getPreference(preferenceId);
        BigDecimal totalBudget = preference.getMaxBudget() == null ? BigDecimal.ZERO : preference.getMaxBudget();
        
        BuildCategory category = preference.getBuildCategory() != null ? preference.getBuildCategory() : BuildCategory.GAMING;
        BudgetAllocation allocation = getBudgetAllocationForCategory(category);
        
        BigDecimal targetCpuBudget = totalBudget.multiply(allocation.cpuPercentage);
        BigDecimal targetGpuBudget = totalBudget.multiply(allocation.gpuPercentage);
        BigDecimal targetRamBudget = totalBudget.multiply(allocation.ramPercentage);
        BigDecimal targetMotherboardBudget = totalBudget.multiply(allocation.motherboardPercentage);
        BigDecimal targetStorageBudget = totalBudget.multiply(allocation.storagePercentage);
        BigDecimal targetPsuBudget = totalBudget.multiply(allocation.psuPercentage);
        BigDecimal targetCoolerBudget = totalBudget.multiply(allocation.coolerPercentage);
        BigDecimal targetCaseBudget = totalBudget.multiply(allocation.casePercentage);

        Cpu cpu = chooseCpu(preference, targetCpuBudget);
        if (cpu == null) {
            cpu = chooseCheapestCpu(preference, totalBudget);
        }

        BigDecimal remainingAfterCpu = totalBudget.subtract(priceOrZero(cpu == null ? null : cpu.getPrice())).max(BigDecimal.ZERO);

        Gpu gpu = chooseGpu(preference, targetGpuBudget.min(remainingAfterCpu));
        if (gpu == null) {
            gpu = chooseCheapestGpu(preference, remainingAfterCpu);
        }

        String cpuSocket = cpu == null ? null : cpu.getSocket();
        BigDecimal remainingAfterGpu = remainingAfterCpu.subtract(priceOrZero(gpu == null ? null : gpu.getPrice())).max(BigDecimal.ZERO);
        
        Motherboard motherboard = chooseMotherboard(
            preference,
            targetMotherboardBudget.min(remainingAfterGpu),
            cpuSocket
        );
        BigDecimal remainingAfterMotherboard = remainingAfterGpu.subtract(priceOrZero(motherboard == null ? null : motherboard.getPrice())).max(BigDecimal.ZERO);

        Ram ram = chooseRam(preference, targetRamBudget.min(remainingAfterMotherboard));
        BigDecimal remainingAfterRam = remainingAfterMotherboard.subtract(priceOrZero(ram == null ? null : ram.getPrice())).max(BigDecimal.ZERO);

        Storage storage = chooseStorage(preference, targetStorageBudget.min(remainingAfterRam));
        BigDecimal remainingAfterStorage = remainingAfterRam.subtract(priceOrZero(storage == null ? null : storage.getPrice())).max(BigDecimal.ZERO);

        Psu psu = choosePsu(preference, targetPsuBudget.min(remainingAfterStorage));
        BigDecimal remainingAfterPsu = remainingAfterStorage.subtract(priceOrZero(psu == null ? null : psu.getPrice())).max(BigDecimal.ZERO);

        Cooler cooler = chooseCooler(preference, targetCoolerBudget.min(remainingAfterPsu));
        BigDecimal remainingAfterCooler = remainingAfterPsu.subtract(priceOrZero(cooler == null ? null : cooler.getPrice())).max(BigDecimal.ZERO);

        Case computerCase = chooseCase(preference, targetCaseBudget.min(remainingAfterCooler));

        BigDecimal total = priceOrZero(cpu == null ? null : cpu.getPrice())
                .add(priceOrZero(gpu == null ? null : gpu.getPrice()))
                .add(priceOrZero(motherboard == null ? null : motherboard.getPrice()))
                .add(priceOrZero(ram == null ? null : ram.getPrice()))
                .add(priceOrZero(storage == null ? null : storage.getPrice()))
                .add(priceOrZero(psu == null ? null : psu.getPrice()))
                .add(priceOrZero(cooler == null ? null : cooler.getPrice()))
                .add(priceOrZero(computerCase == null ? null : computerCase.getPrice()));

        return new RecommendationResponse(cpu, gpu, motherboard, total);
    }

    private BigDecimal priceOrZero(BigDecimal price) {
        return price == null ? BigDecimal.ZERO : price;
    }

    private Cpu chooseCpu(UserPreference preference, BigDecimal budget) {
        List<Cpu> candidates;
        if (preference.getPreferredCpuBrand() != null && !preference.getPreferredCpuBrand().isBlank()) {
            candidates = cpuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredCpuBrand(), budget);
            if (!candidates.isEmpty()) {
                return mostExpensiveCpu(candidates);
            }
        }
        candidates = cpuRepository.findByPriceLessThanEqual(budget);
        return mostExpensiveCpu(candidates);
    }

    private Cpu chooseCheapestCpu(UserPreference preference, BigDecimal budget) {
        List<Cpu> candidates;
        if (preference.getPreferredCpuBrand() != null && !preference.getPreferredCpuBrand().isBlank()) {
            candidates = cpuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredCpuBrand(), budget);
            if (!candidates.isEmpty()) {
                return cheapestCpu(candidates);
            }
        }
        candidates = cpuRepository.findByPriceLessThanEqual(budget);
        return cheapestCpu(candidates);
    }

    private Gpu chooseGpu(UserPreference preference, BigDecimal budget) {
        List<Gpu> candidates;
        if (preference.getPreferredGpuBrand() != null && !preference.getPreferredGpuBrand().isBlank()) {
            candidates = gpuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredGpuBrand(), budget);
            if (!candidates.isEmpty()) {
                return mostExpensiveGpu(candidates);
            }
        }
        candidates = gpuRepository.findByPriceLessThanEqual(budget);
        return mostExpensiveGpu(candidates);
    }

    private Gpu chooseCheapestGpu(UserPreference preference, BigDecimal budget) {
        List<Gpu> candidates;
        if (preference.getPreferredGpuBrand() != null && !preference.getPreferredGpuBrand().isBlank()) {
            candidates = gpuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredGpuBrand(), budget);
            if (!candidates.isEmpty()) {
                return cheapestGpu(candidates);
            }
        }
        candidates = gpuRepository.findByPriceLessThanEqual(budget);
        return cheapestGpu(candidates);
    }

    private Motherboard chooseMotherboard(UserPreference preference, BigDecimal budget, String socket) {
        List<Motherboard> candidates;

        if (socket != null && preference.getPreferredMotherboardBrand() != null
                && !preference.getPreferredMotherboardBrand().isBlank()) {
            candidates = motherboardRepository.findBySocketIgnoreCaseAndBrandIgnoreCaseAndPriceLessThanEqual(
                    socket,
                    preference.getPreferredMotherboardBrand(),
                    budget
            );
            if (!candidates.isEmpty()) {
                return cheapestMotherboard(candidates);
            }
        }

        if (socket != null) {
            candidates = motherboardRepository.findBySocketIgnoreCaseAndPriceLessThanEqual(socket, budget);
            if (!candidates.isEmpty()) {
                return cheapestMotherboard(candidates);
            }
        }

        if (preference.getPreferredMotherboardBrand() != null && !preference.getPreferredMotherboardBrand().isBlank()) {
            candidates = motherboardRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(
                    preference.getPreferredMotherboardBrand(),
                    budget
            );
            if (!candidates.isEmpty()) {
                return cheapestMotherboard(candidates);
            }
        }

        candidates = motherboardRepository.findByPriceLessThanEqual(budget);
        return cheapestMotherboard(candidates);
    }

    private Cpu mostExpensiveCpu(List<Cpu> cpus) {
        return cpus.stream()
                .filter(cpu -> cpu.getPrice() != null)
                .max((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Gpu mostExpensiveGpu(List<Gpu> gpus) {
        return gpus.stream()
                .filter(gpu -> gpu.getPrice() != null)
                .max((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Cpu cheapestCpu(List<Cpu> cpus) {
        return cpus.stream()
                .filter(cpu -> cpu.getPrice() != null)
                .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Gpu cheapestGpu(List<Gpu> gpus) {
        return gpus.stream()
                .filter(gpu -> gpu.getPrice() != null)
                .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Motherboard cheapestMotherboard(List<Motherboard> motherboards) {
        return motherboards.stream()
                .filter(motherboard -> motherboard.getPrice() != null)
                .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Ram chooseRam(UserPreference preference, BigDecimal budget) {
        List<Ram> candidates;
        if (preference.getPreferredRamBrand() != null && !preference.getPreferredRamBrand().isBlank()) {
            candidates = ramRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredRamBrand(), budget);
            if (!candidates.isEmpty()) {
                return mostExpensiveRam(candidates);
            }
        }
        candidates = ramRepository.findByPriceLessThanEqual(budget);
        return mostExpensiveRam(candidates);
    }

    private Storage chooseStorage(UserPreference preference, BigDecimal budget) {
        List<Storage> candidates;
        if (preference.getPreferredStorageBrand() != null && !preference.getPreferredStorageBrand().isBlank()) {
            candidates = storageRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredStorageBrand(), budget);
            if (!candidates.isEmpty()) {
                return mostExpensiveStorage(candidates);
            }
        }
        candidates = storageRepository.findByPriceLessThanEqual(budget);
        return mostExpensiveStorage(candidates);
    }

    private Psu choosePsu(UserPreference preference, BigDecimal budget) {
        List<Psu> candidates;
        if (preference.getPreferredPsuBrand() != null && !preference.getPreferredPsuBrand().isBlank()) {
            candidates = psuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredPsuBrand(), budget);
            if (!candidates.isEmpty()) {
                return cheapestPsu(candidates);
            }
        }
        candidates = psuRepository.findByPriceLessThanEqual(budget);
        return cheapestPsu(candidates);
    }

    private Cooler chooseCooler(UserPreference preference, BigDecimal budget) {
        List<Cooler> candidates;
        if (preference.getPreferredCoolerBrand() != null && !preference.getPreferredCoolerBrand().isBlank()) {
            candidates = coolerRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredCoolerBrand(), budget);
            if (!candidates.isEmpty()) {
                return cheapestCooler(candidates);
            }
        }
        candidates = coolerRepository.findByPriceLessThanEqual(budget);
        return cheapestCooler(candidates);
    }

    private Case chooseCase(UserPreference preference, BigDecimal budget) {
        List<Case> candidates;
        if (preference.getPreferredCaseBrand() != null && !preference.getPreferredCaseBrand().isBlank()) {
            candidates = caseRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredCaseBrand(), budget);
            if (!candidates.isEmpty()) {
                return cheapestCase(candidates);
            }
        }
        candidates = caseRepository.findByPriceLessThanEqual(budget);
        return cheapestCase(candidates);
    }

    private Ram mostExpensiveRam(List<Ram> rams) {
        return rams.stream()
                .filter(ram -> ram.getPrice() != null)
                .max((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Storage mostExpensiveStorage(List<Storage> storages) {
        return storages.stream()
                .filter(storage -> storage.getPrice() != null)
                .max((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Psu cheapestPsu(List<Psu> psus) {
        return psus.stream()
                .filter(psu -> psu.getPrice() != null)
                .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Cooler cheapestCooler(List<Cooler> coolers) {
        return coolers.stream()
                .filter(cooler -> cooler.getPrice() != null)
                .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private Case cheapestCase(List<Case> cases) {
        return cases.stream()
                .filter(computerCase -> computerCase.getPrice() != null)
                .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                .orElse(null);
    }

    private BudgetAllocation getBudgetAllocationForCategory(BuildCategory category) {
        switch (category) {
            case GAMING:
                return new BudgetAllocation(
                    //same as default for now, can be tweaked later if we want to differentiate more
                        new BigDecimal("0.30"), // CPU
                        new BigDecimal("0.50"), // GPU
                        new BigDecimal("0.10"), // RAM
                        new BigDecimal("0.05"), // Motherboard
                        new BigDecimal("0.03"), // Storage
                        new BigDecimal("0.01"), // PSU
                        new BigDecimal("0.01")  // Cooler
                );
            case AI_ML:
                return new BudgetAllocation(
                        new BigDecimal("0.35"), // CPU
                        new BigDecimal("0.45"), // GPU
                        new BigDecimal("0.12"), // RAM
                        new BigDecimal("0.03"), // Motherboard
                        new BigDecimal("0.03"), // Storage
                        new BigDecimal("0.01"), // PSU
                        new BigDecimal("0.01")  // Cooler
                );
            case WORKSTATION:
                return new BudgetAllocation(
                        new BigDecimal("0.40"), // CPU
                        new BigDecimal("0.35"), // GPU
                        new BigDecimal("0.15"), // RAM
                        new BigDecimal("0.05"), // Motherboard
                        new BigDecimal("0.03"), // Storage
                        new BigDecimal("0.01"), // PSU
                        new BigDecimal("0.01")  // Cooler
                );
            default:
                return new BudgetAllocation(
                        new BigDecimal("0.30"), // CPU
                        new BigDecimal("0.50"), // GPU
                        new BigDecimal("0.10"), // RAM
                        new BigDecimal("0.05"), // Motherboard
                        new BigDecimal("0.03"), // Storage
                        new BigDecimal("0.01"), // PSU
                        new BigDecimal("0.01")  // Cooler
                );
        }
    }

    private static class BudgetAllocation {
        BigDecimal cpuPercentage;
        BigDecimal gpuPercentage;
        BigDecimal ramPercentage;
        BigDecimal motherboardPercentage;
        BigDecimal storagePercentage;
        BigDecimal psuPercentage;
        BigDecimal coolerPercentage;
        BigDecimal casePercentage;

        BudgetAllocation(BigDecimal cpu, BigDecimal gpu, BigDecimal ram, BigDecimal motherboard, BigDecimal storage, BigDecimal psu, BigDecimal cooler) {
            this.cpuPercentage = cpu;
            this.gpuPercentage = gpu;
            this.ramPercentage = ram;
            this.motherboardPercentage = motherboard;
            this.storagePercentage = storage;
            this.psuPercentage = psu;
            this.coolerPercentage = cooler;
            // Case gets the remainder
            this.casePercentage = BigDecimal.ONE
                    .subtract(cpu)
                    .subtract(gpu)
                    .subtract(ram)
                    .subtract(motherboard)
                    .subtract(storage)
                    .subtract(psu)
                    .subtract(cooler);
        }
    }
}
