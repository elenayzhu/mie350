package com.team15.partpicker.controller;

import com.team15.partpicker.exception.StorageNotFoundException;
import com.team15.partpicker.model.entity.Storage;
import com.team15.partpicker.model.repository.StorageRepository;
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
@RequestMapping("/storages")
public class StorageController {

    private final StorageRepository storageRepository;

    public StorageController(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @GetMapping
    public List<Storage> getStorages(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return storageRepository.search(query, brand, type, minCapacity, minPrice, maxPrice);
    }

    @GetMapping("/{storageId}")
    public Storage getStorage(@PathVariable @NonNull Long storageId) {
        return storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Storage createStorage(@Valid @RequestBody @NonNull Storage storage) {
        storage.setId(null);
        return storageRepository.save(storage);
    }

    @PutMapping("/{storageId}")
    public Storage updateStorage(@PathVariable @NonNull Long storageId, @Valid @RequestBody @NonNull Storage storage) {
        if (!storageRepository.existsById(storageId)) {
            throw new StorageNotFoundException(storageId);
        }
        storage.setId(storageId);
        return storageRepository.save(storage);
    }

    @DeleteMapping("/{storageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStorage(@PathVariable @NonNull Long storageId) {
        if (!storageRepository.existsById(storageId)) {
            throw new StorageNotFoundException(storageId);
        }
        storageRepository.deleteById(storageId);
    }
}
