package com.team15.partpicker.model.service;

import com.team15.partpicker.controller.RecommendationResponse;
import com.team15.partpicker.exception.BuildNotFoundException;
import com.team15.partpicker.exception.CpuNotFoundException;
import com.team15.partpicker.exception.GpuNotFoundException;
import com.team15.partpicker.exception.MotherboardNotFoundException;
import com.team15.partpicker.exception.UserPreferenceNotFoundException;
import com.team15.partpicker.model.entity.Build;
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
import org.springframework.lang.NonNull;
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
    private final BuildRepository buildRepository;

    public RecommendationService(
            CpuRepository cpuRepository,
            GpuRepository gpuRepository,
            MotherboardRepository motherboardRepository,
            RamRepository ramRepository,
            PsuRepository psuRepository,
            CaseRepository caseRepository,
            StorageRepository storageRepository,
            CoolerRepository coolerRepository,
            UserPreferenceRepository userPreferenceRepository,
            BuildRepository buildRepository
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
        this.buildRepository = buildRepository;
    }

    public List<Cpu> listAllCpus() {
        return cpuRepository.findAll();
    }

    public Cpu getCpu(@NonNull Long cpuId) {
        return cpuRepository.findById(cpuId)
                .orElseThrow(() -> new CpuNotFoundException(cpuId));
    }

    public Cpu addCpu(@NonNull Cpu cpu) {
        return cpuRepository.save(cpu);
    }

    public List<Gpu> listAllGpus() {
        return gpuRepository.findAll();
    }

    public Gpu getGpu(@NonNull Long gpuId) {
        return gpuRepository.findById(gpuId)
                .orElseThrow(() -> new GpuNotFoundException(gpuId));
    }

    public Gpu addGpu(@NonNull Gpu gpu) {
        return gpuRepository.save(gpu);
    }

    public List<Motherboard> listAllMotherboards() {
        return motherboardRepository.findAll();
    }

    public Motherboard getMotherboard(@NonNull Long motherboardId) {
        return motherboardRepository.findById(motherboardId)
                .orElseThrow(() -> new MotherboardNotFoundException(motherboardId));
    }

    public Motherboard addMotherboard(@NonNull Motherboard motherboard) {
        return motherboardRepository.save(motherboard);
    }

    public UserPreference createPreference(@NonNull UserPreference userPreference) {
        return userPreferenceRepository.save(userPreference);
    }

    public UserPreference getPreference(@NonNull Long preferenceId) {
        return userPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new UserPreferenceNotFoundException(preferenceId));
    }

    public List<Build> getBuildsForPreference(@NonNull Long preferenceId) {
        getPreference(preferenceId);
        return buildRepository.findByUserPreferenceIdOrderByCreatedAtDesc(preferenceId);
    }

    public List<Build> getAllBuilds() {
        return buildRepository.findAll();
    }

    public Build getBuild(@NonNull Long buildId) {
        return buildRepository.findById(buildId)
                .orElseThrow(() -> new BuildNotFoundException(buildId));
    }

    public Build createBuild(@NonNull Long preferenceId, @NonNull String buildTitle) {
        RecommendationResponse recommendation = recommendForPreference(preferenceId);
        Build savedBuild = getBuild(recommendation.getBuildId());
        savedBuild.setBuildTitle(buildTitle);
        return buildRepository.save(savedBuild);
    }

    public RecommendationResponse recommendForPreference(@NonNull Long preferenceId) {
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
        if (motherboard == null && remainingAfterGpu.compareTo(targetMotherboardBudget) > 0) {
            // Try again with full remaining budget if target was too restrictive
            motherboard = chooseMotherboard(preference, remainingAfterGpu, cpuSocket);
        }
        BigDecimal remainingAfterMotherboard = remainingAfterGpu.subtract(priceOrZero(motherboard == null ? null : motherboard.getPrice())).max(BigDecimal.ZERO);

        Ram ram = chooseRam(preference, targetRamBudget.min(remainingAfterMotherboard));
        if (ram == null && remainingAfterMotherboard.compareTo(targetRamBudget) > 0) {
            ram = chooseRam(preference, remainingAfterMotherboard);
        }
        BigDecimal remainingAfterRam = remainingAfterMotherboard.subtract(priceOrZero(ram == null ? null : ram.getPrice())).max(BigDecimal.ZERO);

        Storage storage = chooseStorage(preference, targetStorageBudget.min(remainingAfterRam));
        if (storage == null && remainingAfterRam.compareTo(targetStorageBudget) > 0) {
            storage = chooseStorage(preference, remainingAfterRam);
        }
        BigDecimal remainingAfterStorage = remainingAfterRam.subtract(priceOrZero(storage == null ? null : storage.getPrice())).max(BigDecimal.ZERO);
        
        int requiredPsuWattage = calculateRequiredPsuWattage(cpu, gpu);
        Psu psu = choosePsu(preference, targetPsuBudget.min(remainingAfterStorage), requiredPsuWattage);
        if (psu == null && remainingAfterStorage.compareTo(targetPsuBudget) > 0) {
            psu = choosePsu(preference, remainingAfterStorage, requiredPsuWattage);
        }
        BigDecimal remainingAfterPsu = remainingAfterStorage.subtract(priceOrZero(psu == null ? null : psu.getPrice())).max(BigDecimal.ZERO);
           
        Cooler cooler = chooseCooler(preference, targetCoolerBudget.min(remainingAfterPsu), cpuSocket);
        if (cooler == null && remainingAfterPsu.compareTo(targetCoolerBudget) > 0) {
            cooler = chooseCooler(preference, remainingAfterPsu, cpuSocket);
        }
        BigDecimal remainingAfterCooler = remainingAfterPsu.subtract(priceOrZero(cooler == null ? null : cooler.getPrice())).max(BigDecimal.ZERO);

        Case computerCase = chooseCase(preference, targetCaseBudget.min(remainingAfterCooler));
        if (computerCase == null && remainingAfterCooler.compareTo(targetCaseBudget) > 0) {
            computerCase = chooseCase(preference, remainingAfterCooler);
        }

        BigDecimal total = priceOrZero(cpu == null ? null : cpu.getPrice())
                .add(priceOrZero(gpu == null ? null : gpu.getPrice()))
                .add(priceOrZero(motherboard == null ? null : motherboard.getPrice()))
                .add(priceOrZero(ram == null ? null : ram.getPrice()))
                .add(priceOrZero(storage == null ? null : storage.getPrice()))
                .add(priceOrZero(psu == null ? null : psu.getPrice()))
                .add(priceOrZero(cooler == null ? null : cooler.getPrice()))
                .add(priceOrZero(computerCase == null ? null : computerCase.getPrice()));

        Build savedBuild = saveGeneratedBuild(
                preference,
                cpu,
                gpu,
                motherboard,
                ram,
                storage,
                psu,
                cooler,
                computerCase,
                "Recommended Build",
                total
        );

        return new RecommendationResponse(
                savedBuild.getId(),
                preference.getId(),
                cpu,
                gpu,
                motherboard,
                ram,
                storage,
                psu,
                cooler,
                computerCase,
                total,
                savedBuild.getCreatedAt()
        );
    }

    private Build saveGeneratedBuild(
            UserPreference preference,
            Cpu cpu,
            Gpu gpu,
            Motherboard motherboard,
            Ram ram,
            Storage storage,
            Psu psu,
            Cooler cooler,
            Case computerCase,
            String buildTitle,
            BigDecimal totalPrice
    ) {
        Build build = new Build();
        build.setUserPreference(preference);
        build.setBuildTitle(buildTitle);
        build.setCpu(cpu);
        build.setGpu(gpu);
        build.setMotherboard(motherboard);
        build.setRam(ram);
        build.setStorage(storage);
        build.setPsu(psu);
        build.setCooler(cooler);
        build.setComputerCase(computerCase);
        build.setTotalPrice(totalPrice);
        return buildRepository.save(build);
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
            //match motherboard socket and CPU socket
            candidates = candidates.stream()
                    .filter(mb -> socket == null || mb.getSocket().equalsIgnoreCase(socket))
                    .toList();
            if (!candidates.isEmpty()) {
                return cheapestMotherboard(candidates);
            }
        }
        candidates = motherboardRepository.findByPriceLessThanEqual(budget);
        candidates = candidates.stream()
            .filter(mb -> socket == null || mb.getSocket().equalsIgnoreCase(socket))
            .toList();
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

    private Psu choosePsu(UserPreference preference, BigDecimal budget, int requiredWattage) {
        List<Psu> candidates;
        if (preference.getPreferredPsuBrand() != null && !preference.getPreferredPsuBrand().isBlank()) {
            candidates = psuRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredPsuBrand(), budget);
            candidates = candidates.stream()
                .filter(psu -> psu.getWattage() >= requiredWattage)
                .toList();
            if (!candidates.isEmpty()) {
                return cheapestPsu(candidates);
            }
        }
        candidates = psuRepository.findByPriceLessThanEqual(budget);
        candidates = candidates.stream()
            .filter(psu -> psu.getWattage() >= requiredWattage)
            .toList();
        return cheapestPsu(candidates);
    }

    private Cooler chooseCooler(UserPreference preference, BigDecimal budget, String socket) {
        List<Cooler> candidates;
        if (preference.getPreferredCoolerBrand() != null && !preference.getPreferredCoolerBrand().isBlank()) {
            candidates = coolerRepository.findByBrandIgnoreCaseAndPriceLessThanEqual(preference.getPreferredCoolerBrand(), budget);
            candidates = candidates.stream()
                .filter(cooler -> socket == null || (cooler.getSocket() != null && cooler.getSocket().equalsIgnoreCase(socket)))
                .toList();
            if (!candidates.isEmpty()) {
                return cheapestCooler(candidates);
            }
        }
        candidates = coolerRepository.findByPriceLessThanEqual(budget);
        candidates = candidates.stream()
            .filter(cooler -> socket == null || (cooler.getSocket() != null && cooler.getSocket().equalsIgnoreCase(socket)))
            .toList();
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

    //PSU Wattage Estimation Start
    private int estimateCpuWattage(Cpu cpu) { //cpu no actual data so use estimation with core
        if (cpu == null) {
            return 0;
        }
        int cores = cpu.getCores();

        if (cores <= 6) {
            return 95;
        }
        if (cores <= 8) {
            return 125;
        }
        return 170;
    }

    private int estimateGpuWattage(Gpu gpu) { //gpu no actual data so use estimation with vram
        if (gpu == null) {
            return 0;
        }
        int vram = gpu.getVramGb();

        if (vram <= 8) {
            return 150;
        }
        if (vram <= 12) {
            return 220;
        }
        return 300;
    }

    private int calculateRequiredPsuWattage(Cpu cpu, Gpu gpu) {
        int cpuWatts = estimateCpuWattage(cpu);
        int gpuWatts = estimateGpuWattage(gpu);

        int estimatedSystemLoad = cpuWatts + gpuWatts + 100; //motherboard, RAM, storage, fans
        return (int) Math.ceil(estimatedSystemLoad * 1.3);   //30% headroom
    }
    //PSU Wattage Estimation End

    private BudgetAllocation getBudgetAllocationForCategory(BuildCategory category) {
        switch (category) {
            case GAMING:
                return new BudgetAllocation(
                        new BigDecimal("0.25"), // CPU
                        new BigDecimal("0.35"), // GPU
                        new BigDecimal("0.08"), // RAM
                        new BigDecimal("0.12"), // Motherboard
                        new BigDecimal("0.08"), // Storage
                        new BigDecimal("0.06"), // PSU
                        new BigDecimal("0.06")  // Cooler
                );
            case AI_ML:
                return new BudgetAllocation(
                        new BigDecimal("0.28"), // CPU
                        new BigDecimal("0.38"), // GPU
                        new BigDecimal("0.10"), // RAM
                        new BigDecimal("0.10"), // Motherboard
                        new BigDecimal("0.06"), // Storage
                        new BigDecimal("0.04"), // PSU
                        new BigDecimal("0.04")  // Cooler
                );
            case WORKSTATION:
                return new BudgetAllocation(
                        new BigDecimal("0.32"), // CPU
                        new BigDecimal("0.28"), // GPU
                        new BigDecimal("0.12"), // RAM
                        new BigDecimal("0.12"), // Motherboard
                        new BigDecimal("0.08"), // Storage
                        new BigDecimal("0.04"), // PSU
                        new BigDecimal("0.04")  // Cooler
                );
            default:
                return new BudgetAllocation(
                        new BigDecimal("0.25"), // CPU
                        new BigDecimal("0.35"), // GPU
                        new BigDecimal("0.08"), // RAM
                        new BigDecimal("0.12"), // Motherboard
                        new BigDecimal("0.08"), // Storage
                        new BigDecimal("0.06"), // PSU
                        new BigDecimal("0.06")  // Cooler
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
