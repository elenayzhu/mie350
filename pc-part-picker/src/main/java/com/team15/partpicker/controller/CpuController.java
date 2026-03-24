package com.team15.partpicker.controller;

import com.team15.partpicker.exception.CpuNotFoundException;
import com.team15.partpicker.model.entity.Cpu;
import com.team15.partpicker.model.repository.CpuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cpus")
public class CpuController {

    private final CpuRepository cpuRepository;

    public CpuController(CpuRepository cpuRepository) {
        this.cpuRepository = cpuRepository;
    }

    @GetMapping
    public List<Cpu> getCpus(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String socket,
            @RequestParam(required = false) Integer minCores,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return cpuRepository.search(query, brand, socket, minCores, minPrice, maxPrice);
    }

    @GetMapping("/{cpuId}")
    public Cpu getCpu(@PathVariable @NonNull Long cpuId) {
        return cpuRepository.findById(cpuId)
                .orElseThrow(() -> new CpuNotFoundException(cpuId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cpu createCpu(@Valid @RequestBody @NonNull Cpu cpu) {
        cpu.setId(null);
        return cpuRepository.save(cpu);
    }

    @PutMapping("/{cpuId}")
    public Cpu updateCpu(@PathVariable @NonNull Long cpuId, @Valid @RequestBody @NonNull Cpu cpu) {
        if (!cpuRepository.existsById(cpuId)) {
            throw new CpuNotFoundException(cpuId);
        }
        cpu.setId(cpuId);
        return cpuRepository.save(cpu);
    }

    @DeleteMapping("/{cpuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCpu(@PathVariable @NonNull Long cpuId) {
        if (!cpuRepository.existsById(cpuId)) {
            throw new CpuNotFoundException(cpuId);
        }
        cpuRepository.deleteById(cpuId);
    }
}
