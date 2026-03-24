package com.team15.partpicker.controller;

import com.team15.partpicker.controller.request.PartUpdateRequest;
import com.team15.partpicker.exception.CoolerNotFoundException;
import com.team15.partpicker.model.entity.Cooler;
import com.team15.partpicker.model.repository.CoolerRepository;
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
@RequestMapping("/coolers")
public class CoolerController {

    private final CoolerRepository coolerRepository;

    public CoolerController(CoolerRepository coolerRepository) {
        this.coolerRepository = coolerRepository;
    }

    @GetMapping
    public List<Cooler> getCoolers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String socket,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minMaxTdp,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return coolerRepository.search(query, brand, socket, type, minMaxTdp, minPrice, maxPrice);
    }

    @GetMapping("/{coolerId}")
    public Cooler getCooler(@PathVariable @NonNull Long coolerId) {
        return coolerRepository.findById(coolerId)
                .orElseThrow(() -> new CoolerNotFoundException(coolerId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cooler createCooler(@Valid @RequestBody @NonNull Cooler cooler) {
        cooler.setId(null);
        return coolerRepository.save(cooler);
    }

    @PutMapping("/{coolerId}")
    public Cooler updateCooler(
            @PathVariable @NonNull Long coolerId,
            @Valid @RequestBody @NonNull PartUpdateRequest updateRequest
    ) {
        Cooler existingCooler = coolerRepository.findById(coolerId)
                .orElseThrow(() -> new CoolerNotFoundException(coolerId));

        existingCooler.setModel(updateRequest.getModel());
        existingCooler.setBrand(updateRequest.getBrand());
        existingCooler.setPrice(updateRequest.getPrice());

        return coolerRepository.save(existingCooler);
    }

    @DeleteMapping("/{coolerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCooler(@PathVariable @NonNull Long coolerId) {
        if (!coolerRepository.existsById(coolerId)) {
            throw new CoolerNotFoundException(coolerId);
        }
        coolerRepository.deleteById(coolerId);
    }
}
