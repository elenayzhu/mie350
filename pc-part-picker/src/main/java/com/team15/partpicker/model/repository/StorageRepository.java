package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    List<Storage> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Storage> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Storage> findByTypeIgnoreCase(String type);

    List<Storage> findByTypeIgnoreCaseAndPriceLessThanEqual(String type, BigDecimal maxPrice);
}
