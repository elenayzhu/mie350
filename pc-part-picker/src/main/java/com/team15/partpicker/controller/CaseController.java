package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Case;
import com.team15.partpicker.model.repository.CaseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cases")
public class CaseController {

    private final CaseRepository caseRepository;

    public CaseController(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @GetMapping
    public List<Case> getCases() {
        return caseRepository.findAll();
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
