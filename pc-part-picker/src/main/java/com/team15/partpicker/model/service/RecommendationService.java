package com.team15.partpicker.model.service;

import com.team15.partpicker.controller.RecommendationResponse;
import com.team15.partpicker.exception.CpuNotFoundException;
import com.team15.partpicker.exception.GpuNotFoundException;
import com.team15.partpicker.exception.MotherboardNotFoundException;
import com.team15.partpicker.exception.UserPreferenceNotFoundException;
import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.entity.Motherboard;
import com.team15.partpicker.model.entity.UserPreference;
import com.team15.partpicker.model.repository.CpuRepository;
import com.team15.partpicker.model.repository.GpuRepository;
import com.team15.partpicker.model.repository.MotherboardRepository;
import com.team15.partpicker.model.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RecommendationService {

    private final CpuRepository cpuRepository;
    private final GpuRepository gpuRepository;
    private final MotherboardRepository motherboardRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public RecommendationService(
            CpuRepository cpuRepository,
            GpuRepository gpuRepository,
            MotherboardRepository motherboardRepository,
            UserPreferenceRepository userPreferenceRepository
    ) {
        this.cpuRepository = cpuRepository;
        this.gpuRepository = gpuRepository;
        this.motherboardRepository = motherboardRepository;
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

        BigDecimal cpuBudget = totalBudget.multiply(new BigDecimal("0.40"));
        BigDecimal gpuBudget = totalBudget.multiply(new BigDecimal("0.45"));
        BigDecimal motherboardBudget = totalBudget.subtract(cpuBudget).subtract(gpuBudget);

        Cpu cpu = chooseCpu(preference, cpuBudget);
        Gpu gpu = chooseGpu(preference, gpuBudget);

        String cpuSocket = cpu == null ? null : cpu.getSocket();
        Motherboard motherboard = chooseMotherboard(preference, motherboardBudget, cpuSocket);

        BigDecimal total = BigDecimal.ZERO;
        if (cpu != null) {
            total = total.add(cpu.getPrice());
        }
        if (gpu != null) {
            total = total.add(gpu.getPrice());
        }
        if (motherboard != null) {
            total = total.add(motherboard.getPrice());
        }

        return new RecommendationResponse(cpu, gpu, motherboard, total);
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
        return cpus.stream().max((a, b) -> a.getPrice().compareTo(b.getPrice())).orElse(null);
    }

    private Gpu mostExpensiveGpu(List<Gpu> gpus) {
        return gpus.stream().max((a, b) -> a.getPrice().compareTo(b.getPrice())).orElse(null);
    }

    private Motherboard cheapestMotherboard(List<Motherboard> motherboards) {
        return motherboards.stream().min((a, b) -> a.getPrice().compareTo(b.getPrice())).orElse(null);
    }
}
