package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Gpu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface GpuRepository extends JpaRepository<Gpu, Long> {

    List<Gpu> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Gpu> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);
}
