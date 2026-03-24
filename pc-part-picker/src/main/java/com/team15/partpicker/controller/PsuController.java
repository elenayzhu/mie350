package com.team15.partpicker.controller;

import com.team15.partpicker.controller.request.PartUpdateRequest;
import com.team15.partpicker.exception.PsuNotFoundException;
import com.team15.partpicker.model.entity.Psu;
import com.team15.partpicker.model.repository.PsuRepository;
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
@RequestMapping("/psus")
public class PsuController {

    private final PsuRepository psuRepository;

    public PsuController(PsuRepository psuRepository) {
        this.psuRepository = psuRepository;
    }

    @GetMapping
    public List<Psu> getPsus(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer minWattage,
            @RequestParam(required = false) String efficiencyRating,
            @RequestParam(required = false) String modularType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return psuRepository.search(query, brand, minWattage, efficiencyRating, modularType, minPrice, maxPrice);
    }

    @GetMapping("/{psuId}")
    public Psu getPsu(@PathVariable @NonNull Long psuId) {
        return psuRepository.findById(psuId)
                .orElseThrow(() -> new PsuNotFoundException(psuId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Psu createPsu(@Valid @RequestBody @NonNull Psu psu) {
        psu.setId(null);
        return psuRepository.save(psu);
    }

    @PutMapping("/{psuId}")
    public Psu updatePsu(
            @PathVariable @NonNull Long psuId,
            @Valid @RequestBody @NonNull PartUpdateRequest updateRequest
    ) {
        Psu existingPsu = psuRepository.findById(psuId)
                .orElseThrow(() -> new PsuNotFoundException(psuId));

        existingPsu.setModel(updateRequest.getModel());
        existingPsu.setBrand(updateRequest.getBrand());
        existingPsu.setPrice(updateRequest.getPrice());

        return psuRepository.save(existingPsu);
    }

    @DeleteMapping("/{psuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePsu(@PathVariable @NonNull Long psuId) {
        if (!psuRepository.existsById(psuId)) {
            throw new PsuNotFoundException(psuId);
        }
        psuRepository.deleteById(psuId);
    }
}
