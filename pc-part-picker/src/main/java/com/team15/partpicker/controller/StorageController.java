package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Storage;
import com.team15.partpicker.model.repository.StorageRepository;
import org.springframework.lang.NonNull;
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
@RequestMapping("/storages")
public class StorageController {

    private final StorageRepository storageRepository;

    public StorageController(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @GetMapping
    public List<Storage> getStorages(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return storageRepository.findAll().stream()
                .filter(storage -> brand == null || storage.getBrand().equalsIgnoreCase(brand))
                .filter(storage -> type == null || storage.getType().equalsIgnoreCase(type))
                .filter(storage -> minCapacity == null || storage.getCapacityGb() >= minCapacity)
                .filter(storage -> minPrice == null || storage.getPrice().compareTo(minPrice) >= 0)
                .filter(storage -> maxPrice == null || storage.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @GetMapping("/{storageId}")
    public Storage getStorage(@PathVariable @NonNull Long storageId) {
        return storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found with id: " + storageId));
    }

    @PostMapping
    public Storage createStorage(@Valid @RequestBody @NonNull Storage storage) {
        return storageRepository.save(storage);
    }
}
