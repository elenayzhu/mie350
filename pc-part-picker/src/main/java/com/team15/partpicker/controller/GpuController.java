package com.team15.partpicker.controller;

import com.team15.partpicker.controller.request.PartUpdateRequest;
import com.team15.partpicker.exception.GpuNotFoundException;
import com.team15.partpicker.model.entity.Gpu;
import com.team15.partpicker.model.repository.GpuRepository;
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
@RequestMapping("/gpus")
public class GpuController {

    private final GpuRepository gpuRepository;

    public GpuController(GpuRepository gpuRepository) {
        this.gpuRepository = gpuRepository;
    }

    @GetMapping
    public List<Gpu> getGpus(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer minVramGb,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return gpuRepository.search(query, brand, minVramGb, minPrice, maxPrice);
    }

    @GetMapping("/{gpuId}")
    public Gpu getGpu(@PathVariable @NonNull Long gpuId) {
        return gpuRepository.findById(gpuId)
                .orElseThrow(() -> new GpuNotFoundException(gpuId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Gpu createGpu(@Valid @RequestBody @NonNull Gpu gpu) {
        gpu.setId(null);
        return gpuRepository.save(gpu);
    }

    @PutMapping("/{gpuId}")
    public Gpu updateGpu(
            @PathVariable @NonNull Long gpuId,
            @Valid @RequestBody @NonNull PartUpdateRequest updateRequest
    ) {
        Gpu existingGpu = gpuRepository.findById(gpuId)
                .orElseThrow(() -> new GpuNotFoundException(gpuId));

        existingGpu.setModel(updateRequest.getModel());
        existingGpu.setBrand(updateRequest.getBrand());
        existingGpu.setPrice(updateRequest.getPrice());

        return gpuRepository.save(existingGpu);
    }

    @DeleteMapping("/{gpuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGpu(@PathVariable @NonNull Long gpuId) {
        if (!gpuRepository.existsById(gpuId)) {
            throw new GpuNotFoundException(gpuId);
        }
        gpuRepository.deleteById(gpuId);
    }
}
