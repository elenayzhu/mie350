package com.team15.partpicker.controller;

import com.team15.partpicker.exception.CaseNotFoundException;
import com.team15.partpicker.model.entity.Case;
import com.team15.partpicker.model.repository.CaseRepository;
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
@RequestMapping("/cases")
public class CaseController {

    private final CaseRepository caseRepository;

    public CaseController(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @GetMapping
    public List<Case> getCases(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String formFactor,
            @RequestParam(required = false) Integer minMaxGpuLengthMm,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return caseRepository.search(query, brand, formFactor, minMaxGpuLengthMm, minPrice, maxPrice);
    }

    @GetMapping("/{caseId}")
    public Case getCase(@PathVariable @NonNull Long caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseNotFoundException(caseId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Case createCase(@Valid @RequestBody @NonNull Case caseEntity) {
        caseEntity.setId(null);
        return caseRepository.save(caseEntity);
    }

    @PutMapping("/{caseId}")
    public Case updateCase(@PathVariable @NonNull Long caseId, @Valid @RequestBody @NonNull Case caseEntity) {
        if (!caseRepository.existsById(caseId)) {
            throw new CaseNotFoundException(caseId);
        }
        caseEntity.setId(caseId);
        return caseRepository.save(caseEntity);
    }

    @DeleteMapping("/{caseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCase(@PathVariable @NonNull Long caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new CaseNotFoundException(caseId);
        }
        caseRepository.deleteById(caseId);
    }
}
