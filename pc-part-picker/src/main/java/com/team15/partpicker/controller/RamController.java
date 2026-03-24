package com.team15.partpicker.controller;

import com.team15.partpicker.controller.request.PartUpdateRequest;
import com.team15.partpicker.exception.RamNotFoundException;
import com.team15.partpicker.model.entity.Ram;
import com.team15.partpicker.model.repository.RamRepository;
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
@RequestMapping("/rams")
public class RamController {

    private final RamRepository ramRepository;

    public RamController(RamRepository ramRepository) {
        this.ramRepository = ramRepository;
    }

    @GetMapping
    public List<Ram> getRams(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String ddrType,
            @RequestParam(required = false) Integer minSpeed,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return ramRepository.search(query, brand, ddrType, minSpeed, minCapacity, minPrice, maxPrice);
    }

    @GetMapping("/{ramId}")
    public Ram getRam(@PathVariable @NonNull Long ramId) {
        return ramRepository.findById(ramId)
                .orElseThrow(() -> new RamNotFoundException(ramId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ram createRam(@Valid @RequestBody @NonNull Ram ram) {
        ram.setId(null);
        return ramRepository.save(ram);
    }

    @PutMapping("/{ramId}")
    public Ram updateRam(
            @PathVariable @NonNull Long ramId,
            @Valid @RequestBody @NonNull PartUpdateRequest updateRequest
    ) {
        Ram existingRam = ramRepository.findById(ramId)
                .orElseThrow(() -> new RamNotFoundException(ramId));

        existingRam.setModel(updateRequest.getModel());
        existingRam.setBrand(updateRequest.getBrand());
        existingRam.setPrice(updateRequest.getPrice());

        return ramRepository.save(existingRam);
    }

    @DeleteMapping("/{ramId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRam(@PathVariable @NonNull Long ramId) {
        if (!ramRepository.existsById(ramId)) {
            throw new RamNotFoundException(ramId);
        }
        ramRepository.deleteById(ramId);
    }
}
