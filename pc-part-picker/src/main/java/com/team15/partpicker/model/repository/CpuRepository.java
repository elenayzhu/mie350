package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Cpu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CpuRepository extends JpaRepository<Cpu, Long> {

    List<Cpu> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Cpu> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);
}
