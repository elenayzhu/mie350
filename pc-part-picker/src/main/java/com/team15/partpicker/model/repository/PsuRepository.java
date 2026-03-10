package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Psu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PsuRepository extends JpaRepository<Psu, Long> {

    List<Psu> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Psu> findByBrandIgnoreCaseAndPriceLessThanEqual(String brand, BigDecimal maxPrice);

    List<Psu> findByWattageGreaterThanEqual(Integer minWattage);

    List<Psu> findByWattageGreaterThanEqualAndPriceLessThanEqual(Integer minWattage, BigDecimal maxPrice);
}
