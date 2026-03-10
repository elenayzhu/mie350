package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Case> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Case> findByFormFactorIgnoreCase(String formFactor);

    List<Case> findByFormFactorIgnoreCaseAndPriceLessThanEqual(String formFactor, BigDecimal maxPrice);

    List<Case> findByMaxGpuLengthMmGreaterThanEqual(Integer minGpuLength);
}
