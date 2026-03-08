package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Case;
import com.team15.partpicker.model.repository.CaseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cases")
public class CaseController {

    private final CaseRepository caseRepository;

    public CaseController(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @GetMapping
    public List<Case> getCases(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String formFactor,
            @RequestParam(required = false) Integer minMaxGpuLengthMm,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return caseRepository.findAll().stream()
                .filter(caseItem -> brand == null || caseItem.getBrand().equalsIgnoreCase(brand))
                .filter(caseItem -> formFactor == null || caseItem.getFormFactor().equalsIgnoreCase(formFactor))
                .filter(caseItem -> minMaxGpuLengthMm == null || caseItem.getMaxGpuLengthMm() >= minMaxGpuLengthMm)
                .filter(caseItem -> minPrice == null || caseItem.getPrice().compareTo(minPrice) >= 0)
                .filter(caseItem -> maxPrice == null || caseItem.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @GetMapping("/{caseId}")
    public Case getCase(@PathVariable Long caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with id: " + caseId));
    }

    @PostMapping
    public Case createCase(@Valid @RequestBody Case caseEntity) {
        return caseRepository.save(caseEntity);
    }
}
