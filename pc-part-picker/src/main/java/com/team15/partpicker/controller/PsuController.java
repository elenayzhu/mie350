package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Psu;
import com.team15.partpicker.model.repository.PsuRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/psus")
public class PsuController {

    private final PsuRepository psuRepository;

    public PsuController(PsuRepository psuRepository) {
        this.psuRepository = psuRepository;
    }

    @GetMapping
    public List<Psu> getPsus() {
        return psuRepository.findAll();
    }

    @GetMapping("/{psuId}")
    public Psu getPsu(@PathVariable Long psuId) {
        return psuRepository.findById(psuId)
                .orElseThrow(() -> new RuntimeException("PSU not found with id: " + psuId));
    }

    @PostMapping
    public Psu createPsu(@Valid @RequestBody Psu psu) {
        return psuRepository.save(psu);
    }
}
