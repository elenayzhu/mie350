package com.team15.partpicker.controller;

import com.team15.partpicker.model.entity.Storage;
import com.team15.partpicker.model.repository.StorageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/storages")
public class StorageController {

    private final StorageRepository storageRepository;

    public StorageController(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @GetMapping
    public List<Storage> getStorages() {
        return storageRepository.findAll();
    }

    @GetMapping("/{storageId}")
    public Storage getStorage(@PathVariable Long storageId) {
        return storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found with id: " + storageId));
    }

    @PostMapping
    public Storage createStorage(@Valid @RequestBody Storage storage) {
        return storageRepository.save(storage);
    }
}
