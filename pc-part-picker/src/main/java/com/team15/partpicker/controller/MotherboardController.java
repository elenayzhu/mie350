package com.team15.partpicker.controller;

import com.team15.partpicker.exception.MotherboardNotFoundException;
import com.team15.partpicker.model.entity.Motherboard;
import com.team15.partpicker.model.repository.MotherboardRepository;
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
@RequestMapping("/motherboards")
public class MotherboardController {

    private final MotherboardRepository motherboardRepository;

    public MotherboardController(MotherboardRepository motherboardRepository) {
        this.motherboardRepository = motherboardRepository;
    }

    @GetMapping
    public List<Motherboard> getMotherboards(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String socket,
            @RequestParam(required = false) String formFactor,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return motherboardRepository.search(query, brand, socket, formFactor, minPrice, maxPrice);
    }

    @GetMapping("/{motherboardId}")
    public Motherboard getMotherboard(@PathVariable @NonNull Long motherboardId) {
        return motherboardRepository.findById(motherboardId)
                .orElseThrow(() -> new MotherboardNotFoundException(motherboardId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Motherboard createMotherboard(@Valid @RequestBody @NonNull Motherboard motherboard) {
        motherboard.setId(null);
        return motherboardRepository.save(motherboard);
    }

    @PutMapping("/{motherboardId}")
    public Motherboard updateMotherboard(
            @PathVariable @NonNull Long motherboardId,
            @Valid @RequestBody @NonNull Motherboard motherboard
    ) {
        if (!motherboardRepository.existsById(motherboardId)) {
            throw new MotherboardNotFoundException(motherboardId);
        }
        motherboard.setId(motherboardId);
        return motherboardRepository.save(motherboard);
    }

    @DeleteMapping("/{motherboardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMotherboard(@PathVariable @NonNull Long motherboardId) {
        if (!motherboardRepository.existsById(motherboardId)) {
            throw new MotherboardNotFoundException(motherboardId);
        }
        motherboardRepository.deleteById(motherboardId);
    }
}
